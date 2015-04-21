This command "transforms" a given dataset in a package of train-test cross-validation folds. In the way, it may also apply feature selection on the dataset. (Division in folds and feature selection is put in the same command due a logical dependency. Feature selection is done in a fair way only if features are selected over the train dataset and then applied over the test dataset.) It can also be used to apply feature selection over a pre-defined pair of train-test datasets. The command syntax is:

```
java task.DatasetTransformer <-d dataset> <-o output dir> [-targetCategory category] [-numberOfFolds folds|-testDataset test dataset] [-numberOfSelectedFeatures nf] [-featureSelectionMethod differentiation|SAM]
```

, where:

  * _dataset_ is the original dataset to be divided in folds, or the train dataset if instead you are applying a train-test dataset to feature selection.
  * _category_ is the name of the category to be considered as case in the dataset.
  * _folds|test dataset_ is considered to be the number of folds for dividing _dataset_; if it is not a number, then it is considered as the name of a test dataset.
  * _output dir_ is the path of the directory where output datasets will be dumped. If the given directory name does not exist, then it is created.
  * _n_ is the number of features to be selected.
  * _feature selection method_ is the codename of the feature selection methodology to be used. Currently the only one available is _differentiation_, which triggers selection of the top _nf_ most differentiated features according to a simple absolute difference of the average value of the feature in cases and controls.

As usual, optional parameters that are not supplied receive the default values defined by _pipeline.properties_ .

# Examples of usage #

## Simple folding ##

```
java task.DatasetTransformer -d datafiles/ageing.tab -o datafiles/ageing5 -targetCategory old -numberOfFolds 5
```

This command dumps (in the directory _datafiles/ageing3_) the train-test pairs corresponding to a 5-fold cross-validation of _datafiles/ageing.tab_ . That dumping follows a simple, obvious naming convention, as shown below:

```
openbiomind/classes $ ls datafiles/ageing5/
test0.tab       test2.tab       test4.tab       train1.tab      train3.tab
test1.tab       test3.tab       train0.tab      train2.tab      train4.tab
openbiomind/classes $ 
```

In this example we can also see the use of the -targetCategory defining "old" as the case category for this dataset. (The default value for target categories is "CASE" at _pipeline.properties_ )

## Feature selection ##

```
java task.DatasetTransformer -d datafiles/ageing.tab -o datafiles/ageing3-25 -targetCategory old -numberOfSelectedFeatures 25                         
```

Single no folding or test dataset parameter is supplied, this command will follow the default properties and make a 3-folding of the dataset. But this time each fold- dataset contains only the top 25 most differentiated features selected from the train datasets of each fold. (As one can see, the feature selection method was not specified and therefore the default value of "differentiation" defined in pipeline.properties is assumed.) This becomes quite apparent by comparing the number of lines of the original dataset with the ones at the output directory:

```
openbiomind/classes $ wc datafiles/ageing3-25/*      27     214    1836 datafiles/ageing3-25/test0.tab
      27     187    1643 datafiles/ageing3-25/test1.tab
      27     187    1608 datafiles/ageing3-25/test2.tab
      27     349    2946 datafiles/ageing3-25/train0.tab
      27     376    3237 datafiles/ageing3-25/train1.tab
      27     376    3156 datafiles/ageing3-25/train2.tab
     162    1689   14426 total
openbiomind/classes $ wc datafiles/ageing.tab 
   18228  364558 3043992 datafiles/ageing.tab
openbiomind/classes $ 
```

## Feature selection in a predefined train-test pair ##

```
openbiomind/classes $ java task.DatasetTransformer -d datafiles/allamltrain.tab -targetCategory ALL -testDataset datafiles/allamltest.tab -o datafiles/allaml50sam -featureSelectionMethod SAM        27 cases, 11 controls
emac:~/Documents/Meus/Vetta/projetos/openbiomind/classes emac$ wc datafiles/allaml50sam/*
      52    2102   13865 datafiles/allaml50sam/test0.tab
      52    2310   15173 datafiles/allaml50sam/train0.tab
     104    4412   29038 total
emac:~/Documents/Meus/Vetta/projetos/openbiomind/classes emac$ wc datafiles/allamltrain.tab 
    7072  317073 1484330 datafiles/allamltrain.tab
emac:~/Documents/Meus/Vetta/projetos/openbiomind/classes emac$ 
```

# Final comments #

Data directories as those dumped by _DatasetTransformer_ are used as input for other commands, for example MetaTask.