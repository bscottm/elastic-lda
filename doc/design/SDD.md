# Elastic-LDA software design document

## Background

This is the software design document for an [Elasticsearch] _Latent Dirichlet Allocation_-based topic modeling plugin.
The project was inspired by a conversation between two US Navy officers who support a Office of Naval Research Reserve
Component's _"Technology Futures and Forecasting"_ project. This plugin is actually written by those same two US Navy
officers, both of whom are also Computer Science PhDs.

There are a lot of papers that explain _LDA_ in great academic detail; for a good overview, see [the Wikipedia
article][WikiLDA]. In a nutshell, _LDA_ is a numeric algorithm that infers a set of topics from a corpus, aka a set of
documents. The basic process for producing a topic model is:

1. Create a corpus dictionary
    - Preprocess each document.

        This includes removing stop words, eliminating hyphenation (if possible, this is hard to do), removing text that
        looks like chapter headers (whenever possible), singularizing plural words, etc. The result of this phase is the
        "bag of words" for each document.

    - Create a bijective mapping between each unique word in the corpus' documents to a number (identifier).
2. Convert the corpus from its textual format into its corresponding numeric matrix format.
3. Run the _LDA_ algorithm over the numeric matrix to generate the topic model **for that corpus**.
4. Convert the numeric topic model matrix back into a human-readable form via the corpus dictionary.

The Python-based [gensim] package provides a good introduction and examples on how to manually perform _LDA_ over a
corpus. [gensim] does not perform any preprocessing; you have to build your corpus as a Python list of word lists before
you execute _LDA_. [gensim] will handle converting your list of word lists into a corpus dictionary **after** you
preprocess your documents.

## Use Cases

1. The basic use case for this plugin is generating the topic models for a _type_ within an Elasticsearch _index_.
   [Elasticsearch] provides a RESTful interface wherein _index_ and _type_ are the top two levels of a namespace that
   identify a group of documents, viz. `http://localhost:9200/index/type/document`.

    The core idea is that an _index_'s _type_ provides documents that are the _LDA_ corpus for which a topic model
    is generated.

    The data modeler or Elasticsearch developer creates a mapping on a type, similar to how the [mapper attachments]
    [mapper-attachments] plugin operates:

    ```javascript
    PUT /test/myindex/mytype/_mapping
    {
    	"mytype": {
    		"properties": {
    			"mytext": "string",
    			"lda_result" : { "type": "lda_topic_model", "origin": "mytext" }
    		}
    	}
    }
    ```

    where `lda_result` is the designated document property in which the _LDA_ topics are stored.

2. Query by topic keywords, topic relevance and return a list of documents.

    A document stored in an [Elasticsearch] _index/type_ has two additional properties that respectively store a list of
    topics and a list of topic relevances. The _ElasticLDA_ plugin adds these properties to the document via
    [Elasticsearch]'s index mapping API when the index is initially created.

    ```json
    {
    	"lda_result": {
      		"lda_topics": [ "blue", "cheese", "danish", "specialty" ],
    		"lda_relevance": [ 0.1024, 0.10101, 0.002, 0.001 ]
    	}
    }
    ```

    `(topic, relevance)` tuples can be regenerated via a `zip` ([Python example][zip]). A destructuring `unzip` function
    splits the two lists apart to maintain ordering between the two lists. Javascript does not support a native tuple type.

    An alternative representation is a list of relevance/topic objects:

    ```json
    {
    	"lda_result": [
    		{ "topic": "blue", "relevance": 0.1024 },
    		{ "topic": "cheese", "relevance": 0.10101 },
    		{ "topic": "danish", "relevance": 0.002 },
    		{ "topic": "speciality", "relevance": 0.001 }
    	]
    }
    ```

    Which is the correct implementation depends on usability testing. The second implementation result is probably
    better for applying arithmetic operators in a query, e.g., `lda_result.relevance >= 0.05`.

    The rationale for adding these properties is convenience. Otherwise, the user would have to perform additional
    queries in a different _type_ or _index/type_ to perform a relational join to determine which topics correspond to a
    document.

3. Query by topic model, returning the topic model and document relevance.

    "Document relevance" in this use case refers to the strength of the relationship between a document and the
    individual topic. An individual topic may have more than one associated document from the corpus; it will always
    one association with a document (one-to-many mapping). An example document for this use case might look like:

    ```json
    {
   		"topic": {
   			"words": [ "blue", "cheese", "danish", "speciality" ],
   			"relevance": [ 0.1024, 0.10101, 0.002, 0.001 ],
   			"documents": [
   				{ "id": "myindex/mytype/doc001", "doc_relevance": 0.999 },
   				{ "id": "myindex/mytype/doc002", "doc_relevance": 0.402 },
   				{ "id": "myindex/mytype/doc013", "doc_relevance": 0.100 }
   			]
   		}
    }
    ```

    In this simple example, the topic is "blue cheese danish specialty", which has three associated documents.
    `myindex/mytype/doc001` is strongly associated with this topic (0.999), whereas `myindex/mytype/doc013` is weakly
    associated with the topic (0.100).

    This use case allows a querier to search based on topic keywords, document relevance. It requires a separate _type_
    stored under the index that is unique from all of the other types the data modeler or Elasticsearch developer embeds
    in an _index_. These meta-types would be generated by plugin when the _index_'s mapping is initially defined by
    reserving all names starting with a specific prefix, e.g., _elda_, to generate the meta-type name, e.g., _elda\_mytype_.

[ElasticSearch]: http://www.elasticsearch.org/
[WikiLDA]: https://en.wikipedia.org/wiki/Latent_Dirichlet_allocation
[gensim]: http://radimrehurek.com/gensim/
[zip]: https://docs.python.org/3.3/library/functions.html#zip
[mapper-attachments]: https://github.com/elasticsearch/elasticsearch-mapper-attachments