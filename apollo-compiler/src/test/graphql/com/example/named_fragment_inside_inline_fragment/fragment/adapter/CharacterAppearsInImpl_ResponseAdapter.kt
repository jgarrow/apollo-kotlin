// AUTO-GENERATED FILE. DO NOT MODIFY.
//
// This class was automatically generated by Apollo GraphQL plugin from the GraphQL queries it found.
// It should not be modified by hand.
//
package com.example.named_fragment_inside_inline_fragment.fragment.adapter

import com.apollographql.apollo.api.ResponseField
import com.apollographql.apollo.api.internal.ResponseAdapter
import com.apollographql.apollo.api.internal.ResponseReader
import com.apollographql.apollo.api.internal.ResponseWriter
import com.example.named_fragment_inside_inline_fragment.fragment.CharacterAppearsInImpl
import com.example.named_fragment_inside_inline_fragment.type.Episode
import kotlin.Array
import kotlin.String
import kotlin.Suppress
import kotlin.collections.List

@Suppress("NAME_SHADOWING", "UNUSED_ANONYMOUS_PARAMETER", "LocalVariableName",
    "RemoveExplicitTypeArguments", "NestedLambdaShadowedImplicitParameter", "PropertyName",
    "RemoveRedundantQualifierName")
object CharacterAppearsInImpl_ResponseAdapter : ResponseAdapter<CharacterAppearsInImpl.Data> {
  val RESPONSE_FIELDS: Array<ResponseField> = arrayOf(
    ResponseField(
      type = ResponseField.Type.NotNull(ResponseField.Type.Named.Other("String")),
      responseName = "__typename",
      fieldName = "__typename",
      arguments = emptyMap(),
      conditions = emptyList(),
      fieldSets = emptyList(),
    ),
    ResponseField(
      type =
          ResponseField.Type.NotNull(ResponseField.Type.List(ResponseField.Type.Named.Other("Episode"))),
      responseName = "appearsIn",
      fieldName = "appearsIn",
      arguments = emptyMap(),
      conditions = emptyList(),
      fieldSets = emptyList(),
    )
  )

  override fun fromResponse(reader: ResponseReader, __typename: String?):
      CharacterAppearsInImpl.Data {
    return reader.run {
      var __typename: String? = __typename
      var appearsIn: List<Episode?>? = null
      while(true) {
        when (selectField(RESPONSE_FIELDS)) {
          0 -> __typename = readString(RESPONSE_FIELDS[0])
          1 -> appearsIn = readList<Episode>(RESPONSE_FIELDS[1]) { reader ->
            Episode.safeValueOf(reader.readString())
          }
          else -> break
        }
      }
      CharacterAppearsInImpl.Data(
        __typename = __typename!!,
        appearsIn = appearsIn!!
      )
    }
  }

  override fun toResponse(writer: ResponseWriter, value: CharacterAppearsInImpl.Data) {
    writer.writeString(RESPONSE_FIELDS[0], value.__typename)
    writer.writeList(RESPONSE_FIELDS[1], value.appearsIn) { value, listItemWriter ->
      listItemWriter.writeString(value?.rawValue)}
  }
}
