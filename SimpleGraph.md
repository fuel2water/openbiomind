#Usage and "philosophy" of the SimpleGraph command

# Overview #

The SimpleGraph command as added to the OpenBiomind toolkit as a simpler (and in some ways better) alternative to the original GraphFeatures command.

Utilization of GraphFeatures over many different datasets showed some difficults in usability and a tendency for producing less-than-ideal results. GraphFeatures has a relatively large number of parameters, and usually many attempts tuning those parameters are necessary until a satisfactory result is achieved. Even though, the final result often may come short of being ideal, often forcing the user to opt between a graph fully connected but not very clear due to an excess of edges, or a graph will fewer edges but on the other hand broken in several subgraphs.

SimpleGraph addresses those shortcomings by using only one parameter and leaving most of the work of producing a good-looking, clear and informative graph to internal heuristics and systematics. The philosophy and usage of the command are detailed below.

# Criteria and Solutions #

SimpleGraph was built keeping in mind the production of a good-looking, clear graph. Of course that first definition is rather vague and subjective, but the list below tries to break down in key points what defines a graph as good looking and clear:

  1. The graph should be connected, i.e., with at least one path between any two pairs of nodes.
  1. Edges should be kept to a minimum, avoiding the formation of a "spaguetti" graph.

SimpleGraph achieves criterion #2 as well as the requisite of simple parameters by showing just the n most co-occurring edges (as well as their corresponding nodes, of course) of the metatask result analyzed.

That is, the number of edges is parametrized in a near-hard way. It is not a _completely_ hard way only because there may be ties among the top n most occurring edges, and so n is in fact a lower bound. Also, the larger the n, the higher-than-n will tend to be the actual number of edges.

Also, plotting only the most co-occuring edges almost always will produce a disconnected graph, violating criterion #1. Therefore, more edges are necessary for "glueing" the several independent subgraphs that usually appear under that initial approach.

The solution used here was to use co-expression edges to perform such "glueing". For a given independent subgraph A, the most co-expressive edge between a node of A and another node from one of the other subgraphs is found, until all subgraphs are united in a single connected graph.

# Usage #

The syntax of the SimpleGraph command is given by

```
java task.SimpleGraph <-h horizontal dataset> <-m mobra dataset> <-u utility file> <-o output file> [-minEdges minimum number of edges]
```

As one may suspect, options -h, -m, -u and -o are identical to the same options used by GraphFeatures. On the other hand, the three graph-controlling options of GraphFeatures are "substituted" by just one, -minEdges, which sets the minimum number of edges of the graph as defined in the previous section.

If the -mindEdges option is ommited, a default value of 10 edges at least is assumed. (The corresponding value can of course be editted in pipeline.properties file.)

The usage example below produces a graph setting minEdges to 12:

```
openbiomind/bin $ java task.SimpleGraph -h horizontal.tab  -m mobra.tab  -u utable.txt -o graphcal.dot -minEdges 16
Loading utable.txt
Loading mobra.tab
Loading horizontal.tab
openbiomind/bin $ head graphcal.dot
graph G {
"2410015N17Rik" [
label = "{2410015N17Rik | RIKEN cDNA 2410015N17 gene | {2.4324324% | 9 | 83 | 56}}",
shape = "record"
];
"Fubp1" [
label = "{Fubp1 | far upstream element (FUSE) binding protein 1 | {1.6216216% | 12 | 92 | 79}}",
shape = "record"
];
"2410015N17Rik" -- "Fubp1"
openbiomind/bin $ dot -Tpng graphcal.dot > graphcal.png
openbiomind/bin $ 
```

As we can see above, SimpleGraph is used to generate a file specifying a graph in DOT language. Then, a PNG image (shown below) is generated from such description, using the dot command from the Graphviz package.

<img src='http://img503.imageshack.us/img503/7564/graphcal.png' width='1200'>