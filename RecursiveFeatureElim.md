The RecursiveFeatureElim command allows the user to run many classification experiments (called _tasks_ in OpenBiomind lingo) on a given dataset folding, in which the reduced feature-set that is derived from every round of MetaTask performed is ''re-transforms'' the data-set folding. Therefore, the process selects recursively for the most utilized gene features in each round of classification, such it may come up with the most important feature-set in the end.

It follows the syntax below:


```
java task.RecursiveFeatureElim <-f fold directory> <-d base dataset> <-o outpath> [-numberOfTasks number of tasks] [-targetCategory case category] [-classificationMethod snpga|boolsimple|snplocal|snp|conventional|boolcomplex] [-n number of trials] [-e cut off percentage]
```


Notes:

  * d specifies the original file containing the original dataset and gene-oncology information.
  * f specifies the path of a directory containing train/test dataset pairs, in the way of DatasetTransformer.
  * o  specifies a valid directory where the RFE trials and their respective metatask output results will be stored.
  * numberOfTasks specifies the number of single tasks in a given metatask round to be run. (Default value 3 from _pipeline.properties_ is assumed if option is ommited.)
  * _-targetCategory_  is the name of the case category in the datasets. (Default value CASE from _pipeline.properties_ is assumed if option is ommited.)
  * _-classificationMethod_ specifies the technique used for generating a classification model (see MetaTask for more detailed information on each classification method.)
  * -n_specifies the number of RFE trials to be ran. This is different from numberOfTasks as numberOfTasks specify the tasks to be run in a single RFE trial. Users may wish to run multiple RFE trials as to get the best reduced feature-set as RFE process can be quite stochastic.
  * -e_ specifies the RFE feature selection cut-off rate, that is after a single metatask round; what percentile of features utilized by the last metatask round will be used for the next round of RFE (i.e., 90% cut-off-rate denotes that 90% of the top utilized genes will be selected for next round).

---+ An example of use

```
openbiomind/classes $ -d "datafiles/ext.go.pir.varm126.tab" -f "datafiles/varm126-3/" -o "datafiles/varm126-rfe" -numberOfTasks 20 -e 0.85 -n 5                                   
RFE Trial#0:
Running recursive feature elimination round 1: 1581 features ...
Round 1 completed; Average GP Performance: 0.87445885
Running recursive feature elimination round 2: 42 features ...
Round 2 completed; Average GP Performance: 0.97727275
Running recursive feature elimination round 3: 22 features ...
Round 3 completed; Average GP Performance: 0.97727275
Running recursive feature elimination round 4: 17 features ...
Round 4 completed; Average GP Performance: 0.97727275
Running recursive feature elimination round 5: 14 features ...
Round 5 completed; Average GP Performance: 0.9848485
Running recursive feature elimination round 6: 11 features ...
Round 6 completed; Average GP Performance: 0.9848485
(Full terminal output ommited for sake of space ...)
The best feature set is found in datafiles/varm126-rfe/trial3/13.txt. Its performance was: 1.0. Its feature-set size was: 3.
/openbiomind/classes $
```

_datafiles/varm126-3/_ contains a 3-folding of dataset _varm126.tab_ included in the deploy package. The execution of the RFE above takes about fifteen minutes to complete in a Windowx XP/Celeron 2.25Mhz, 512MB machine. At the end, all output files are in the specificed output dir:

```
openbiomind/classes emac$ ls datafiles/varm126-rfe/
trial0/        trial1/        trial2/        trial3/
trial4/        
openbiomind/classes emac$ 
```

However, we are interested in the best feature-set (the best classification prediction, smallest size) that the RFE came up with. According to the program output, the best feature-set is stored in datafiles/varm126-rfe/trials3/13.txt. To see the list of the genes, one only has to simply open up the feature-set, as pointed by the filepath.

```
openbiomind/classes emac$ datafiles/varm126-rfe/trial3/13.txt
Mrpl12	70.149254	1	1	33	mitochondrial ribosomal protein L12
GO:0016491	64.17911	2	1	22	oxidoreductase activity; molecular_function
Rpl19	37.313435	3	1	24	ribosomal protein L19
```

If we want to see the classification models evolved in this RFE round that culminated this feature-set and the resulting performance; we simply go to the classification model path, in the format $OUTPUT\_DIR/$best\_trial/$best\_round/outfinal.txt.

```
openbiomind/classes emac$ datafiles/varm126-rfe/trial3/13/outfinal.txt
Fold 0:
Ensemble:
Model #0:
or
 inputThreshold GO:0016491 447.6821
 inputThreshold Rpl22l1 3683.1
Model #1:
or
 inputThreshold GO:0005761 1262.8
 inputThreshold Rpl22l1 3683.1
Model #2:
or
 inputThreshold Rpl22l1 3683.1
 inputThreshold GO:0005761 1262.8

(Full terminal output ommited for sake of space ...)
```