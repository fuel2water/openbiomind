OpenBiomind is a toolkit for analysis of gene expression, SNP and other biological datasets using advanced machine learning and pattern mining techniques, and includes traditional clustering, hybrid clustering, and other techniques.

OpenBiomind is modular and command-line driven. Components communicate via standardized file formats. OpenBiomind [commands](CommandList.md) cover:

> •	Dataset enhancement, with information extracted from gene and protein ontologies, as well as other dataset treatments/transformations (such as feature selection and creation of validation folds)

> •	Multiple classification model generation, for a given dataset using several modalities of GP.

> •	Important Feature computation

> •	Clustering using direct expression or MOBRA/MUTIC transformations

> •	Cluster visualization (conventional, raster,color-coded)

> •	Graph vizualization showing various inter-relationships among features: co-expression, co-occurrence and utility and differentiation ranks

> •	Multiple "pipelines" - sequences of chained commands where output of one or more commands is used as the input for a different command, including a "complete pipeline" command were all possible chains are explored.

OpenBiomind is developed in Java and designed for portability and simplicity. OpenBiomind file formats (for data and for results) are human-readable plaintext and easily manipulatable with other standard tools such as the GNU coreutils, sed, awk, perl, python, etc.