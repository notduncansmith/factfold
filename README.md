# Factfold

Factfold is a Clojure library that provides facilities for describing and executing dependent computation graphs.

Specifically, it:

1. Turns a dataflow DAG into a fold function
2. Executes this fold function continuously over a dynamic collection of parallel input streams
3. Continuously groups and filters these fold results

## Why does this exist

Most programs can now be performantly implemented with the fold-over-immutable-log architecture, meaning all we need from the programmer is the fold function.

Factfold makes it both simpler and easier to specify this fold function and be confident in its correctness, by breaking it down into labeled dependent steps.

In the future, by providing debugging/backup/recovery utilities, we can develop and deploy robust production applications while concerning our application code almost exclusively with the domain.

Critically, the above should be available without having to operate 3+ services (Zookeeper, Kafka, some database, some K/V cache, etc) in addition to one's actual application. Most of the value of this architecture is not the increased volume or speed of data processing capable, but the decreased complexity in the description/operation/evolution of the processing while maintaining competitive performance.

## How

Facts are immutable observations about the world. These are roughly analogous to paper notes or documents.

Cases are collections of related facts that can be reasoned about independently. These are roughly analogous to "files" as popularly portrayed (manilla folders full of related notes/documents).

Models are collections of related properties that can be derived from the facts of a given case. These properties can also be derived from each other, but only in one direction. Models are specified as dependency ordered arrays of maps, each of which pairs keyword property names with functions to compute their values.

## Usage

The general production service workflow:

0. Define models as dataflow DAGs
1. Load cases from your storage mechanism of choice.
2. Execute models against facts from each case
3. Incrementally update case states with new facts as available
4. Query resulting state


Copyright Duncan Smith 2017