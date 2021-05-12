package com.apollographql.apollo3.compiler.introspection

import com.apollographql.apollo3.graphql.ast.ConversionException
import com.apollographql.apollo3.graphql.ast.GQLArgument
import com.apollographql.apollo3.graphql.ast.GQLArguments
import com.apollographql.apollo3.graphql.ast.GQLBooleanValue
import com.apollographql.apollo3.graphql.ast.GQLDirective
import com.apollographql.apollo3.graphql.ast.GQLDocument
import com.apollographql.apollo3.graphql.ast.GQLEnumTypeDefinition
import com.apollographql.apollo3.graphql.ast.GQLEnumValueDefinition
import com.apollographql.apollo3.graphql.ast.GQLFieldDefinition
import com.apollographql.apollo3.graphql.ast.GQLFloatValue
import com.apollographql.apollo3.graphql.ast.GQLInputObjectTypeDefinition
import com.apollographql.apollo3.graphql.ast.GQLInputValueDefinition
import com.apollographql.apollo3.graphql.ast.GQLIntValue
import com.apollographql.apollo3.graphql.ast.GQLInterfaceTypeDefinition
import com.apollographql.apollo3.graphql.ast.GQLListType
import com.apollographql.apollo3.graphql.ast.GQLListValue
import com.apollographql.apollo3.graphql.ast.GQLNamedType
import com.apollographql.apollo3.graphql.ast.GQLNonNullType
import com.apollographql.apollo3.graphql.ast.GQLObjectField
import com.apollographql.apollo3.graphql.ast.GQLObjectTypeDefinition
import com.apollographql.apollo3.graphql.ast.GQLObjectValue
import com.apollographql.apollo3.graphql.ast.GQLOperationTypeDefinition
import com.apollographql.apollo3.graphql.ast.GQLScalarTypeDefinition
import com.apollographql.apollo3.graphql.ast.GQLSchemaDefinition
import com.apollographql.apollo3.graphql.ast.GQLStringValue
import com.apollographql.apollo3.graphql.ast.GQLType
import com.apollographql.apollo3.graphql.ast.GQLUnionTypeDefinition
import com.apollographql.apollo3.graphql.ast.GQLValue
import com.apollographql.apollo3.graphql.ast.Schema
import com.apollographql.apollo3.graphql.ast.parseAsGraphQLValue
import com.apollographql.apollo3.graphql.ast.toSchema
import com.apollographql.apollo3.graphql.ast.withBuiltinDefinitions
import com.apollographql.apollo3.graphql.ast.withoutBuiltinDefinitions

private class GQLDocumentBuilder(private val introspectionSchema: IntrospectionSchema) {

  fun toGQLDocument(): GQLDocument {
    return with(introspectionSchema.__schema) {
      GQLDocument(
          definitions = types.map {
            when (it) {
              is IntrospectionSchema.Schema.Type.Union -> it.toGQLUnionTypeDefinition()
              is IntrospectionSchema.Schema.Type.Interface -> it.toGQLInterfaceTypeDefinition()
              is IntrospectionSchema.Schema.Type.Enum -> it.toGQLEnumTypeDefinition()
              is IntrospectionSchema.Schema.Type.Object -> it.toGQLObjectTypeDefinition()
              is IntrospectionSchema.Schema.Type.InputObject -> it.toGQLInputObjectTypeDefinition()
              is IntrospectionSchema.Schema.Type.Scalar -> it.toGQLScalarTypeDefinition()
            }
          } + schemaDefinition(),
          filePath = null
      )
    }
  }

  private fun IntrospectionSchema.Schema.Type.Object.toGQLObjectTypeDefinition(): GQLObjectTypeDefinition {
    return GQLObjectTypeDefinition(
        description = description,
        name = name,
        directives = emptyList(),
        fields = fields?.map { it.toGQLFieldDefinition() } ?: throw ConversionException("Object '$name' did not define any field"),
        implementsInterfaces = findInterfacesImplementedBy(name)
    )
  }

  private fun findInterfacesImplementedBy(name: String): List<String> {
    return introspectionSchema.__schema.types.filterIsInstance<IntrospectionSchema.Schema.Type.Interface>()
        .filter {
          it.possibleTypes?.map { it.name }?.contains(name) == true
        }
        .map {
          it.name
        }
  }

  private fun IntrospectionSchema.Schema.Type.Enum.toGQLEnumTypeDefinition(): GQLEnumTypeDefinition {
    return GQLEnumTypeDefinition(
        description = description,
        name = name,
        enumValues = enumValues.map { it.toGQLEnumValueDefinition() },
        directives = emptyList()
    )
  }

  private fun IntrospectionSchema.Schema.Type.Enum.Value.toGQLEnumValueDefinition(): GQLEnumValueDefinition {
    return GQLEnumValueDefinition(
        description = description,
        name = name,
        directives = makeDirectives(deprecationReason)
    )
  }

  private fun IntrospectionSchema.Schema.Type.Interface.toGQLInterfaceTypeDefinition(): GQLInterfaceTypeDefinition {
    return GQLInterfaceTypeDefinition(
        name = name,
        description = description,
        fields = fields?.map { it.toGQLFieldDefinition() } ?: throw ConversionException("interface '$name' did not define any field"),
        implementsInterfaces = emptyList(), // TODO
        directives = emptyList()
    )
  }

  private fun IntrospectionSchema.Schema.Field.toGQLFieldDefinition(): GQLFieldDefinition {
    return GQLFieldDefinition(
        name = name,
        description = description,
        arguments = this.args.map { it.toGQLInputValueDefinition() },
        directives = makeDirectives(deprecationReason),
        type = type.toGQLType()
    )
  }

  private fun IntrospectionSchema.Schema.Field.Argument.toGQLInputValueDefinition(): GQLInputValueDefinition {
    return GQLInputValueDefinition(
        name = name,
        description = description,
        directives = makeDirectives(deprecationReason),
        defaultValue = defaultValue.toGQLValue(),
        type = type.toGQLType(),
    )
  }

  private fun Any?.toGQLValue(): GQLValue? {
    if (this == null) {
      // no default value
      return null
    }
    try {
      if (this is String) {
        return parseAsGraphQLValue().getOrThrow()
      }
    } catch (e: Exception) {
      println("Wrongly encoded default value: $this: ${e.message}")
    }

    // All the below should theoretically not happen because the spec says defaultValue should be
    // a GQLValue encoded as a string
    return when {
      this is String -> GQLStringValue(value = this)
      this is Int -> GQLIntValue(value = this)
      this is Long -> GQLIntValue(value = this.toInt())
      this is Double -> GQLFloatValue(value = this)
      this is Boolean -> GQLBooleanValue(value = this)
      this is Map<*, *> -> GQLObjectValue(fields = this.map {
        GQLObjectField(name = it.key as String, value = it.value.toGQLValue()!!)
      })
      this is List<*> -> GQLListValue(values = map { it.toGQLValue()!! })
      else -> throw ConversionException("cannot convert $this to a GQLValue")
    }
  }

  fun makeDirectives(deprecationReason: String?): List<GQLDirective> {
    if (deprecationReason == null) {
      return emptyList()
    }
    return listOf(
        GQLDirective(
            name = "deprecated",
            arguments = GQLArguments(listOf(
                GQLArgument(name = "reason", value = GQLStringValue(value = deprecationReason))
            ))
        )
    )
  }

  private fun IntrospectionSchema.Schema.Type.Union.toGQLUnionTypeDefinition(): GQLUnionTypeDefinition {
    return GQLUnionTypeDefinition(
        name = name,
        description = "",
        memberTypes = possibleTypes?.map { it.toGQLNamedType() } ?: throw ConversionException("Union '$name' must have members"),
        directives = emptyList(),
    )
  }

  private fun IntrospectionSchema.Schema.TypeRef.toGQLNamedType(): GQLNamedType {
    return toGQLType() as? GQLNamedType ?: throw ConversionException("expected a NamedType")
  }

  private fun IntrospectionSchema.Schema.TypeRef.toGQLType(): GQLType {
    return when (this.kind) {
      IntrospectionSchema.Schema.Kind.NON_NULL -> GQLNonNullType(
          type = ofType?.toGQLType() ?: throw ConversionException("ofType must not be null for non null types")
      )
      IntrospectionSchema.Schema.Kind.LIST -> GQLListType(
          type = ofType?.toGQLType() ?: throw ConversionException("ofType must not be null for list types")
      )
      else -> GQLNamedType(
          name = name!!
      )
    }
  }

  private fun IntrospectionSchema.Schema.schemaDefinition(): GQLSchemaDefinition {
    val rootOperationTypeDefinitions = mutableListOf<GQLOperationTypeDefinition>()
    rootOperationTypeDefinitions.add(
        GQLOperationTypeDefinition(
            operationType = "query",
            namedType = queryType.name
        )
    )
    if (mutationType != null) {
      rootOperationTypeDefinitions.add(
          GQLOperationTypeDefinition(
              operationType = "mutation",
              namedType = mutationType.name
          )
      )
    }
    if (subscriptionType != null) {
      rootOperationTypeDefinitions.add(
          GQLOperationTypeDefinition(
              operationType = "subscription",
              namedType = subscriptionType.name
          )
      )
    }

    return GQLSchemaDefinition(
        description = "",
        directives = emptyList(),
        rootOperationTypeDefinitions = rootOperationTypeDefinitions
    )
  }

  private fun IntrospectionSchema.Schema.Type.InputObject.toGQLInputObjectTypeDefinition(): GQLInputObjectTypeDefinition {
    return GQLInputObjectTypeDefinition(
        description = description,
        name = name,
        inputFields = inputFields.map { it.toGQLInputValueDefinition() },
        directives = emptyList()
    )
  }

  private fun IntrospectionSchema.Schema.InputField.toGQLInputValueDefinition(): GQLInputValueDefinition {
    return GQLInputValueDefinition(
        description = description,
        name = name,
        directives = makeDirectives(deprecationReason),
        type = type.toGQLType(),
        defaultValue = defaultValue.toGQLValue()
    )
  }

  private fun IntrospectionSchema.Schema.Type.Scalar.toGQLScalarTypeDefinition(): GQLScalarTypeDefinition {
    return GQLScalarTypeDefinition(
        description = description,
        name = name,
        directives = emptyList()
    )
  }
}

fun IntrospectionSchema.toGQLDocument(): GQLDocument = GQLDocumentBuilder(this).toGQLDocument()

fun IntrospectionSchema.toSchema(): Schema = toGQLDocument()
    /**
     * Introspection already contains builtin types like Int, Boolean, __Schema, etc...
     * This is slightly off as it also remove directives which will have no effect here
     * as they are not stored in introspection
     */
    .withoutBuiltinDefinitions()
    /**
     * toSchema will add the builtin types and directives
     */
    .toSchema()

