// AUTO-GENERATED FILE. DO NOT MODIFY.
//
// This class was automatically generated by Apollo GraphQL plugin from the GraphQL queries it found.
// It should not be modified by hand.
//
package com.example.simple_fragment

import com.apollographql.apollo.api.Operation
import com.apollographql.apollo.api.Query
import com.apollographql.apollo.api.ResponseField
import com.apollographql.apollo.api.internal.QueryDocumentMinifier
import com.apollographql.apollo.api.internal.ResponseAdapter
import com.example.simple_fragment.adapter.TestQuery_ResponseAdapter
import com.example.simple_fragment.fragment.HeroDetails
import com.example.simple_fragment.fragment.HumanDetails
import kotlin.String
import kotlin.Suppress
import kotlin.collections.List

/**
 * Demonstration of Java / Kotlin docs generation
 * for both query and fragments
 */
@Suppress("NAME_SHADOWING", "UNUSED_ANONYMOUS_PARAMETER", "LocalVariableName",
    "RemoveExplicitTypeArguments", "NestedLambdaShadowedImplicitParameter", "PropertyName",
    "RemoveRedundantQualifierName")
internal class TestQuery : Query<TestQuery.Data> {
  override fun operationId(): String = OPERATION_ID

  override fun queryDocument(): String = QUERY_DOCUMENT

  override fun variables(): Operation.Variables = Operation.EMPTY_VARIABLES

  override fun name(): String = OPERATION_NAME

  override fun adapter(): ResponseAdapter<Data> = TestQuery_ResponseAdapter
  override fun responseFields(): List<ResponseField.FieldSet> = listOf(
    ResponseField.FieldSet(null, TestQuery_ResponseAdapter.RESPONSE_FIELDS)
  )
  /**
   * The query type, represents all of the entry points into our object graph
   */
  data class Data(
    val hero: Hero?
  ) : Operation.Data {
    /**
     * A character from the Star Wars universe
     */
    interface Hero {
      val __typename: String

      data class CharacterHero(
        override val __typename: String
      ) : Hero, HeroDetails

      data class CharacterHumanHero(
        override val __typename: String,
        /**
         * What this human calls themselves
         */
        override val name: String
      ) : Hero, HeroDetails, HeroDetails.Human, HumanDetails

      data class OtherHero(
        override val __typename: String
      ) : Hero

      companion object {
        fun Hero.asCharacterHero(): CharacterHero? = this as? CharacterHero

        fun Hero.asCharacterHumanHero(): CharacterHumanHero? = this as? CharacterHumanHero
      }
    }
  }

  companion object {
    const val OPERATION_ID: String =
        "6664b1abfa01e39c63ec2cbb5c6a25012b3fdf7b1064a1ff1172018c0e309828"

    val QUERY_DOCUMENT: String = QueryDocumentMinifier.minify(
          """
          |query TestQuery {
          |  hero {
          |    __typename
          |    ...HeroDetails
          |    ...HumanDetails
          |  }
          |}
          |fragment HeroDetails on Character {
          |  __typename
          |  ...HumanDetails
          |}
          |fragment HumanDetails on Human {
          |  __typename
          |  name
          |}
          """.trimMargin()
        )

    val OPERATION_NAME: String = "TestQuery"
  }
}
