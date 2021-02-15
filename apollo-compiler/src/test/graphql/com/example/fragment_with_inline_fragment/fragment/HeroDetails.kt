// AUTO-GENERATED FILE. DO NOT MODIFY.
//
// This class was automatically generated by Apollo GraphQL plugin from the GraphQL queries it found.
// It should not be modified by hand.
//
package com.example.fragment_with_inline_fragment.fragment

import kotlin.Int
import kotlin.String
import kotlin.Suppress
import kotlin.collections.List

@Suppress("NAME_SHADOWING", "UNUSED_ANONYMOUS_PARAMETER", "LocalVariableName",
    "RemoveExplicitTypeArguments", "NestedLambdaShadowedImplicitParameter", "PropertyName",
    "RemoveRedundantQualifierName")
interface HeroDetails {
  val __typename: String

  /**
   * The name of the character
   */
  val name: String

  /**
   * The friends of the character exposed as a connection with edges
   */
  val friendsConnection: FriendsConnection

  /**
   * A connection object for a character's friends
   */
  interface FriendsConnection {
    /**
     * The total number of friends
     */
    val totalCount: Int?

    /**
     * The edges for each of the character's friends.
     */
    val edges: List<Edges?>?

    /**
     * An edge object for a character's friends
     */
    interface Edges {
      /**
       * The character represented by this friendship edge
       */
      val node: Node?

      /**
       * A character from the Star Wars universe
       */
      interface Node {
        /**
         * The name of the character
         */
        val name: String
      }
    }
  }

  interface Droid : HeroDetails {
    /**
     * The friends of the character exposed as a connection with edges
     */
    override val friendsConnection: FriendsConnection

    /**
     * A connection object for a character's friends
     */
    interface FriendsConnection : HeroDetails.FriendsConnection {
      /**
       * The edges for each of the character's friends.
       */
      override val edges: List<Edges?>?

      /**
       * An edge object for a character's friends
       */
      interface Edges : HeroDetails.FriendsConnection.Edges {
        /**
         * The character represented by this friendship edge
         */
        override val node: Node?

        /**
         * A character from the Star Wars universe
         */
        interface Node : HeroDetails.FriendsConnection.Edges.Node
      }
    }

    interface Droid : HeroDetails.Droid, DroidDetails {
      /**
       * The friends of the character exposed as a connection with edges
       */
      override val friendsConnection: FriendsConnection

      /**
       * A connection object for a character's friends
       */
      interface FriendsConnection : HeroDetails.FriendsConnection,
          HeroDetails.Droid.FriendsConnection {
        /**
         * The edges for each of the character's friends.
         */
        override val edges: List<Edges?>?

        /**
         * An edge object for a character's friends
         */
        interface Edges : HeroDetails.FriendsConnection.Edges,
            HeroDetails.Droid.FriendsConnection.Edges {
          /**
           * The character represented by this friendship edge
           */
          override val node: Node?

          /**
           * A character from the Star Wars universe
           */
          interface Node : HeroDetails.FriendsConnection.Edges.Node,
              HeroDetails.Droid.FriendsConnection.Edges.Node
        }
      }
    }
  }

  interface Human : HeroDetails, HumanDetails {
    /**
     * The friends of the character exposed as a connection with edges
     */
    override val friendsConnection: FriendsConnection

    /**
     * A connection object for a character's friends
     */
    interface FriendsConnection : HeroDetails.FriendsConnection {
      /**
       * The edges for each of the character's friends.
       */
      override val edges: List<Edges?>?

      /**
       * An edge object for a character's friends
       */
      interface Edges : HeroDetails.FriendsConnection.Edges {
        /**
         * The character represented by this friendship edge
         */
        override val node: Node?

        /**
         * A character from the Star Wars universe
         */
        interface Node : HeroDetails.FriendsConnection.Edges.Node
      }
    }
  }

  companion object {
    val FRAGMENT_DEFINITION: String = """
        |fragment HeroDetails on Character {
        |  __typename
        |  ...HumanDetails
        |  ... on Droid {
        |    __typename
        |    ...DroidDetails
        |  }
        |  name
        |  friendsConnection {
        |    totalCount
        |    edges {
        |      node {
        |        name
        |      }
        |    }
        |  }
        |}
        """.trimMargin()

    fun HeroDetails.asDroid(): Droid? = this as? Droid

    fun HeroDetails.asHuman(): Human? = this as? Human

    fun HeroDetails.humanDetails(): HumanDetails? = this as? HumanDetails
  }
}
