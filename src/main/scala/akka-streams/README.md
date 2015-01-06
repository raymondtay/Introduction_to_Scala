
# Design Principles Behind Akka Streams

## What shall users of Akka Streams expect ?

Akka is built upon a conscious decision to offer APIs that are minimal and consistent - as opposed to easy or intuitive.
The credo is that we favor explicitness over magic and if we provide a feature then it must work always, no exceptions.
Another way to say this is that we minimize the number of rules a user has to learn instead of trying to keep the rules
close to what we think users might expect. 

From this follows that the principles implemented by Akka streams are:
+ all features are explicit in the API, no magic
+ supreme compositionality combined pieces retain the function of each part
+ exhaustive model fo the domain of distributed bounded stream processing

This means that we provide all the tools necessary to express any stream processing topology, that we 
model all the essential aspects of this domain (back pressure, buffering, transformations, failure recovery etc) 
and that whatever the user builds is reusable in a larger context.

## Resulting implementation constraints

Compositionality  entials resuability of partial stream topologies, which led us to the lifted approach
of describing data flows as partial flow graphds that can as composite sources, flows and sinks of data. These
building blocks shall then be freely shareable, with the ability to combine them freely to form larger flows. 
The representation of these pieces must therefore be an immutable blueprint that is materialized in an explicit
step in order to start the stream processing, the resulting stream processing engine is then also immutable in the
sense of having a fixed topology that is prescribed by the blueprint, dyanmic networks needs to be modeled by explicitly 
using the reactive streams interfaces for plugging different engines together.

The process of materialization may be parameterized e.g. instantiating a blueprint for handling a TCP connection's data
with specific information about the connection's address and port information. Additionally, materializatiorn will often
create specific objects that are useful to interact with the processing engine onece it is running, for example for shutting it
down or for extracting metrics. This means that the materialization function takes a set of parameters from the outside
and it produces a set of results. Compositionality demands that these two sets cannot interact, because that would
establishe a covert channel by which different pieces would communicate, leading to problems of initialization order and 
inscrutable runtime failures. 

Another aspect of materialization is that we want to support distributed processing, meaning that both the 
parameters and the results need to be location transparent - either serializable immutable values or ActorRefs. 

