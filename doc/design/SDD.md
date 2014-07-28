# Elastic-LDA software design document

## Background

This is the software design document for an [Elasticsearch] _Latent Dirichlet Allocation_-based topic modeling plugin. The project was inspired by a conversation between two US Navy officers who support a Office of Naval Research Reserve Component's "_Technology Futures and Forecasting_" activity. This package is actually written by those same two US Navy officers.

There are a lot of papers that explain _LDA_ in great academic detail; for a good overview, see [the Wikipedia article][WikiLDA]. In a nutshell, _LDA_ is a numeric algorithm that infers a set of topics from a corpus, aka a set of documents. The basic process for producing a topic model is:

1. Create a corpus dictionary
    - Preprocess each document. This includes removing stop words, eliminating hyphenation (if possible, this is hard to do), removing text that looks like chapter headers (whenever possible), singularizing plural words, etc. The result of this phase is the "bag of words" for each document.
    - Create a bijective mapping between each unique word in the corpus' documents to a number (identifier).
2. Convert the corpus from its textual format into its corresponding numeric matrix format.
3. Run the _LDA_ algorithm over the numeric matrix to generate the topic model *for that corpus**.
4. Convert the numeric topic model matrix back into a human-usable form via the corpus dictionary.

The Python-based [gensim] package provides a good introduction and examples on how to manually perform _LDA_ over a corpus. [gensim] does not perform any preprocessing; you have to build your corpus as a Python list of word lists before you execute _LDA_. [gensim] will handle converting your list of word lists into a corpus dictionary **after* you preprocess your documents.

## Use Case

The basic use case for this plugin is generating the topic models for a "type" within an Elasticsearch index. The core idea is that the index provides documents that are the topic model's corpus. The plugin's user is a data modeler or Elasticsearch developer.

- The user configures 

## Elasticsearch index settings and parameter settings

Stuff about the index.

## Typical usage

Typical usage here...

[ElasticSearch]: http://www.elasticsearch.org/
[WikiLDA]: https://en.wikipedia.org/wiki/Latent_Dirichlet_allocation
[gensim]: http://radimrehurek.com/gensim/