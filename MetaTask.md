The MetaTask command runs many classification experiments (called _tasks_ in OpenBiomind lingo) on the same dataset folding. It follows the syntax below:

```
java task.MetaTask <-d dataset directory> <-o outpath> [-numberOfTasks number of tasks] [-targetCategory case category] [-classificationMethod snpga|boolsimple|snplocal|snp|conventional|boolcomplex] [-metataskShuffling on|off]
```

Notes:

  * _-d_ specifies the path of a directory containing train/test dataset pairs, in the way of DatasetTransformer.
  * _-o_  specifies a valid directory where single-task and metatask results will be stored.
  * _-numberOfTasks_ specifies the number of single tasks to be run. (Default value 3 from _pipeline.properties_ is assumed if option is ommited.)
  * _-targetCategory_  is the name of the case category in the datasets. (Default value CASE from _pipeline.properties_ is assumed if option is ommited.)
  * -classificationMethod specifies the technique used for generating a classification model (default value _boolcomplex_ from _pipeline.properties_ is assumed if option is ommited.):
    * _boolsimple_ corresponds to "MOSES-like" GP - boolean operators, median-based input handlers, parsimony-enforcing fitness function, etc.
    * _boolcomplex_ is GP using the same operators annd input handlers from _boolsimple_, but there is no parsimony pressure built in the fitness function (and therefore models can be more complex, hence the name).
    * _conventional_ is GP evolving automata using arithmetic operations, under a f-measure-based fitness function.
    * _snpga_ evolves Pattern-Strength Classifiers by using a GA (Genetic Algorithm) approach.
    * _snplocal_ generates Pattern-Strength Classifiers by using local search.
    * _snp_ is GP specifically targeted to SNP data. It uses input handlers able to target the conventional values for discrete SNP pairings (discussed in the page for command _FoldSelectSNPs_). Other than that, _snp_ uses the same setup of _boolcomplex_.
    * _-metataskShuffling_ is used for permutation analysis. (Specially useful in the case of SNPs, where classification accuracies are usually low and permutation is used to estimate significance of results.) The default value for metatask shuffling in _pipeline.properties_ is _off_.

---+ An example of use

```
openbiomind/classes $ java task.MetaTask -d datafiles/transformed/ -o outputs/test                                   
25 features
Generation 0, best fitness: 20,000000
Generation 1, best fitness: 20,000000
Generation 2, best fitness: 20,000000
Generation 3, best fitness: 20,000000
Generation 4, best fitness: 20,500000
Generation 5, best fitness: 20,500000
(Full terminal output ommited for sake of space)
/openbiomind/classes $
```

_datafiles/transformed/_ contains a 3-folding of dataset _varm126.tab_ included in the deploy package. The execution of the metatask above takes about two minutes to complete in a Mac OS X/PowerPC G4 1.25Mhz machine. At the end, all output files are in the specificed output dir:

```
openbiomind/classes emac$ ls outputs/test/
out0.txt        out1.txt        out2.txt        outfinal.txt
openbiomind/classes emac$ 
```

This example is toyish in the sense that it uses the default value of 3 tasks; typically "serious" metatasks use from 100 to 1,000 tasks. Anyhow, in this example one can see the obvious name convention that assigns out _i_ .txt as the name of the output file of the _i_-th task. Each single-task output file contains crude, text-based representations of the ensemble of models generated for each validation fold, along with their confusion matrices, plus the cross-validated confusion matrices in the end. An example:

```
openbiomind/classes $ more outputs/test/out2.txt 
Fold #0:
Ensemble:
Model #0:
inputThreshold NM_001845 124.616
Train Matrix: 
5       1
2       4
Accuracy: 0.75
Test Matrix: 
2       0
4       1
Accuracy: 0.42857143
Fold #1:
Ensemble:
Model #0:
or
 inputThreshold 1996_s_at 641.864
 inputThreshold NM_003076 165.82365
Model #1:
or
 inputThreshold 38396_at 2484.67
 inputThreshold NM_003076 165.82365
Train Matrix: 
3       2
1       7
Accuracy: 0.7692308
Test Matrix: 
2       1
1       2
Accuracy: 0.6666667
Fold #2:
Ensemble:
Model #0:
not
 inputThreshold 32067_at 99.2621
Train Matrix: 
4       1
2       6
Accuracy: 0.7692308
Test Matrix: 
2       1
2       1
Accuracy: 0.5
Global Train: 
12      4
5       17
Accuracy: 0.7631579
Global Test: 
6       2
7       4
Accuracy: 0.5263158
openbiomind/classes $ 
```

The ensemble for each fold contains the best models produced during the evolution - and so often it has more than one model, as seen in Fold #1. Ensembles perform classification by simple voting of the component models.

The metatask combines results from all single tasks using "fair metatasking". That is, for each fold the smallest best models according to their quality over the training set are gathered and mounted in an ensemble for that fold. Only then the ensemble thus produced is applied to the test set. This combined result is then dumped (in the same format of single tasks) in a special outfinal.txt metatask file, as seen below:

```
openbiomind/classes $ more outputs/test/outfinal.txt 
Fold 0:
Ensemble:
Model #0:
inputThreshold NM_001845 124.616
Train Matrix:
5       1
2       4
Accuracy: 0.75
Test Matrix:
2       0
4       1
Accuracy: 0.42857143
Fold 1:
Ensemble:
Model #0:
or
 not
  inputThreshold NM_005213 14.8367
 inputThreshold NM_002952 1579.01
Model #1:
or
 not
  inputThreshold NM_005213 14.8367
 inputThreshold NM_012317 965.048
Train Matrix:
4       1
0       8
Accuracy: 0.9230769
Test Matrix:
1       2
3       0
Accuracy: 0.16666667
Fold 2:
Ensemble:
Model #0:
or
 not
  inputThreshold NM_178311 170.764
 inputThreshold NM_006660 108.031
Train Matrix:
3       2
0       8
Accuracy: 0.84615386
Test Matrix:
1       2
1       2
Accuracy: 0.5
Global Train Matrix:
12      4
2       20
Accuracy: 0.84210527
Global Test Matrix:
4       4
8       3
Accuracy: 0.36842105
openbiomind/classes $ 
```