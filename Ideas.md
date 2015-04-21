### Java GUI ###

Many biologists using microarrayers and other tools are command-line-phobic. A simple Java GUI (like Weka) could significantly improve OpenBiomind's usability lower the barrier to entry. At a minimum, the Java GUI should allow users to select parameters, launch processes and manage pipelines.

### Recursive feature selection ###

One important and interesting project involves recursive feature selection. OpenBiomind? now contains innovative methods for finding the most important genes associated with a categorial (gene expression or SNP) dataset. One can try interpreting these important genes as a feature set, and then re-running the categorial analysis methods, presumably getting even higher accuracy. Lather, rinse, repeat. This may be a way of getting classification accuracies on gene expression and SNP datasets that beat current OpenBiomind? results, which in turn beat all published results on many datasets. This is a good project for someone with an interest in exploring machine learning and supervised classification in a practical context. Experimentation could involve datasets on cancer, Alzheimer's Disease and calorie restriction which have already been analysed in the OpenBiomind? system.

### Genetic profiling for predicting disease ###

Combining results from multiple SNP experiments to predict the real probability that a person with a given genetic profile will get a certain disease. 23andMe and a number of competing firms are utilizing tests that predict, for instance, a person's odds of getting prostate cancer based on their SNP profile. These predictions involve combination of results from various experiments done by various researchers. The way this combination is currently performed is quite crude and can be substantially improved by use of more sophisticated mathematical methods, which may be fruitfully done within the OpenBiomind? framework. So this is a chance to implement some new bioinformatics that may have a real impact on how disease probabilities are assessed by the numerous commercial companies in this emerging space. Some understanding of genetics will be helpful here, as well as probability theory and Java coding skills.

### Neurobiological data analysis ###

Add necessary data types, algorithms, documented methodologies and other facilities for analysis of neurobiological data as might be suitable for analysis of raw data coming from various studies relating to the [Human Cognome Project](http://en.wikipedia.org/wiki/Human_Cognome_Project)