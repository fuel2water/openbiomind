CompletePipeline performs the "usual" OpenBiomind operations on a given dataset (or pair of train and test datasets), encapsulating in a pipeline (hence the name) the inputs and outputs of multiple commands in the way described below. For sake of simplicity, let's consider the case of single dataset (as opposed to a train-test pair):

  * Enhances dataset.
  * Creates train-test validation folds from dataset.
  * Runs a metatask using the validation folds as inputs.
  * Computes feature utilities on the output of the metatask.
  * Runs vertical and horizontal gene expression clustering on the dataset.
  * Runs MUTIC and MOBRA clustering on the metatask output, plus corresponding image generation.
  * Creates graph showing relationships between the top important features.

All results produced by CompletePipeline - including intermediary files and the such - are stored in a rigid tree, using standardized names, under the output directory specified for the pipeline. For example, here is the list of contents of such a directory:

```
openbiomind/classes $ ls -la outputs/pipelined/
total 4632
drwxr-xr-x   19 emac  emac     646 Oct 18 19:54 .
drwxr-xr-x   18 emac  emac     612 Oct 18 20:14 ..
-rw-r--r--    1 emac  emac  494241 Oct 18 19:51 enhanced_dataset.txt
-rw-r--r--    1 emac  emac    1904 Oct 18 19:52 featureUtility.txt
-rw-r--r--    1 emac  emac   12403 Oct 18 19:54 graph.dot
-rw-r--r--    1 emac  emac  643872 Oct 18 19:54 horizontal.png
-rw-r--r--    1 emac  emac  494026 Oct 18 19:52 horizontal_dataset.txt
-rw-r--r--    1 emac  emac  118496 Oct 18 19:53 horizontal_output.txt
drwxr-xr-x   13 emac  emac     442 Oct 18 18:24 metatask
-rw-r--r--    1 emac  emac    8501 Oct 18 19:54 mobra.png
-rw-r--r--    1 emac  emac    7125 Oct 18 19:54 mobra_dataset.txt
-rw-r--r--    1 emac  emac    2151 Oct 18 19:54 mobra_output.txt
-rw-r--r--    1 emac  emac    7251 Oct 18 19:54 mutic.png
-rw-r--r--    1 emac  emac    2878 Oct 18 19:54 mutic_dataset.txt
-rw-r--r--    1 emac  emac    1935 Oct 18 19:54 mutic_output.txt
drwxr-xr-x    8 emac  emac     272 Oct  3 19:10 transformedDataset
-rw-r--r--    1 emac  emac  127246 Oct 18 19:54 vertical.png
-rw-r--r--    1 emac  emac  417667 Oct 18 19:54 vertical_dataset.txt
-rw-r--r--    1 emac  emac     964 Oct 18 19:54 vertical_output.txt
openbiomind/classes $ 
```

All the names used above are standardized - that is, CompletePipeline will **always** produce files and directories named like that for a single dataset as input. (in the case of a train and test datasets supplied as input, an additional file, _enhanced\_test\_dataset.txt_, will also be present.) And here is the meaning of each of those files and directories:

  * _enhanced\_dataset.txt_: dataset augmented with ontology-related features. (GO and PIR, by default.)
  * _featureUtility.txt_: list of useful features computed from metatask results.
  * _graph.dot_ : a Dot file, viewable using Graphviz, describing a graph of co-expression and co-occurrences between the top useful features. (From version 0.80 on, this graph is internally generated using the SimpleGraph command, while in older versions GraphFeatures was used.)
  * _horizontal\_dataset.txt_: horizontal gene expression dataset made from original categorial dataset presented as input. (If instead a train-test pair was supplied was input, only the train dataset is used.)
  * _horizontal\_output.txt_: output of horizontal gene expression clustering.
  * _metatask_: directory containing metatask outputs.
  * _mobra\_dataset.txt_: MOBRA clustering dataset computed from metatask results.
  * _mobra\_output.txt_: output of MOBRA clustering.
  * _mutic\_dataset.txt_: MUTIC clustering dataset computed from metatask results.
  * _mutic\_output.txt_: output of MUTIC clustering.
  * _transformedDataset_: cross-validation folds created from _enhanced\_dataset.txt_. (If a train-test pair was supplied instead of a single dataset, then this directory contains the enhanced version of the train-test pair.)
  * _vertical\_dataset.txt_: vertical gene expression dataset made from original categorial dataset presented as input. (If instead a train-test pair was supplied was input, only the train dataset is used.)
  * _vertical\_output.txt_: output of vertical gene expression clustering.
  * all _.png_ files: image representations of the horizontal, vertical, MUTIC and MOBRA clusterings, named correspondingly.

As one may imagine, CompletePipeline involves the use of **lots** of parameters for the many OpenBiomind commands composing its processing flow. Fortunately, those parameters are presented in a handy way in the form of a _properties file_. The default property file is included into the OpenBiomind executables .jar package. Here is a view of the default property file:

```
minEdges = 10
topNUseful = 10
topNCooc = 1
topNCoex = 1
datasetClusteringMetric = Euclidean
classificationMethod = boolcomplex
isFolded = true
numberOfFolds = 3
isFeatureSelected = false
numberOfTasks = 3
ontologyAssociationFile = datafiles/go/gene2go.mouse.txt
ontologyDescriptionFile = datafiles/go/go2desc.txt
targetCategory = CASE
featureSelectionMethod = differentiation
numberOfSelectedFeatures = 50
clusteringColors = traditional
metataskShuffling = off
snpSelectionShuffle = off
```

As one can see, most of the property names above are kind of self-explaining. Besides, they are already explained on the documentation of command-line options for all single commands integrating the pipeline. Perhaps of more interest for CompletePipeline are four other parameters, two optional and the other two mandatory. A call to CompletePipeline has the syntax:

```
java task.CompletePipeline <-d dataset> <-o output directory> [-dt test dataset]  [-p property file] [property options]
```

Using the default configurations, a call to complete pipeline will be as simple as, for instance:

```
java -Xmx256m task.CompletePipeline -d datafiles/varm126.tab -o outputs/pipelined
```

(Note: the _-Xmx256m_  is a Java virtual machine parameter setting up heap space. It's use is recommended for the phase of image generation of the CompletePipeline, which usually uses a lot of memory and may crash the JVM without specific settings.)

_property options_ are simply options corresponding directly to the properties in the property file. By evoking a property option, one can override the file value of the corresponding property. For instance, if one wants a realistic number of 1,000 tasks instead of just 3 (as in the default property file), then the _numberOfTasks_ property can be overriden as in the example

```
java -Xmx256m task.CompletePipeline -d datafiles/varm126.tab -o outputs/pipelined -numberOfTasks 1000
```

Alternatively, if one wants property values different from those of the default property, then a different property file can be specified for the pipeline, as in the example below:

```
java -Xmx256m task.CompletePipeline -d datafiles/varm126.tab -o outputs/pipelined -p my_pipeline.properties
```

Finally, the special case of train-test pair of datasets is handled by the -dt option, as shown in the following example:

```
java -Xmx256m task.CompletePipeline -d datafiles/train.tab -dt datafiles/test.tab -o outputs/pipelined
```