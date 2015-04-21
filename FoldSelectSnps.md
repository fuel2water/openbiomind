_FoldSelectSNPs_ is much like a version of DatasetTransformer specifically adapted to deal with SNP datasets.

SNPs, or Single-Nucleotide Polymorphisms, basically represent allelic information focused of the level of individual bases instead of whole genes. A SNP value will be thus typically represented by pairs of symbols representing possible allelic pairings, for instance AA, AB and BB. Therefore, SNP data is _discrete_, in oposition to gene expression data, which is represented by continuous numeric values.

Therefore, the OpenBiomind format for SNP datasets is extremely similar to the gene expression data, with just two differences:

  * Instead of numbers values of feature x sample are pairs of characters. Character '0'  is considered special, meaning a "no call" or "indetermined" SNP.
  * There is no description column. While description columns often make sense as a way to describe a gene in an expression dataset, SNPs rarely (if ever) have particular descriptions, and are often analysed relatively to the genes of which they are part.

Therefore, the structure of an OpenBiomind SNP dataset with _n_ samples and _m_ SNPs is:

```
         \t sample id 1 \t sample id 2 \t ... \t sample id n
snp id 1 \t pair 1,1    \t pair 1,2    \t ... \t pair 1,n
snp id 2 \t pair 2,1    \t pair 2,2    \t ... \t pair 2,n
.
.
.
snp id m \t pair m,1    \t pair m,2    \t ... \t pair m,n
```

And the upper-left "corner" of a real OpenBiomind SNP dataset looks like:

```
              MAYO_10139 MAYO_10198 MAYO_102246 ...
              1          2          2           ...
SNP_A-2009808 42         42         00          ...
SNP_A-2260965 31         31         11          ...
SNP_A-4231989 22         22         22          ...
.
.
.
```

The convention of pairs of characters (numbers in the case above) meaning pairs of bases is found in other formats like PED/MAP and may help the user in the task of scripting an OpenBiomind base dataset file from other sources.

Anyhow, _FoldSelectSNPs_, among other transformations such as folding and selection, in fact converts the base format described above into a "pseudo-numeric" format exactly equal to the one used for gene expression data, as will be shown ahead. That somehow awkward format transformation is a design option chosen for dealing with historical circumstances: since most of the framework of OpenBiomind was designed for dealing at first with gene expression data, using a pseudo-float representation for SNPs allows the user the possibility of applying many of the pre-existing OpenBiomind features to SNP data.

The syntax of the FoldSelectSNPs command is:

```
java task.FoldSelectSNPs <-d snp dataset> <-o outpath> [-targetCategory case category] [-numberOfSelectedFeatures number of selected SNPs] [-numberOfFolds number of folds] [-snpSelectionShuffle on|off]
```

As usual, most of the options are pre-defined in the _pipeline.properties_ file. Most of the options above have the same meaning that they do in commands like MetaTask or DatasetTransformer. The _-snpSelectionShuffle_ is trickier and will be discussed later on this page with an example.

First, let's see a basic utilization of _FoldSelectSNPs_:

```
$ java -cp .:openbiomind-bin_0.60.jar task.FoldSelectSNPs -d datafiles/geneconverted_affy.snp -o outputs/testsnp/ -targetCategory 2
Infile: datafiles/geneconverted_affy.snp
nFolds: 3
nFeatures: 50
outdir: outputs/testsnp/
targetCategory: 2
shuffle: false
Adding SNP_A-2009808
Adding SNP_A-2009808
Adding SNP_A-2009808
[...]
Adding SNP_A-2254065
Adding SNP_A-2254065
Adding SNP_A-2254065
Replacing 0.4644381 by 0.50910383
Replacing 0.46816644 by 0.52001554
Replacing 0.45985425 by 0.50100297
[...]
Replacing 0.5247328 by 0.53595096
Replacing 0.5248828 by 0.5373477
Replacing 0.5255226 by 0.5331529
$ ls outputs/testsnp/
base_dataset.tab        test2.tab               train2.tab
test0.tab               train0.tab
test1.tab               train1.tab
$ wc datafiles/geneconverted_affy.snp 
    1056 1491070 4493870 datafiles/geneconverted_affy.snp
$ wc outputs/testsnp/*
     126  177910  826046 outputs/testsnp/base_dataset.tab
      52   24592  117115 outputs/testsnp/test0.tab
      52   24540  116572 outputs/testsnp/test1.tab
      52   24540  116103 outputs/testsnp/test2.tab
      52   48980  231155 outputs/testsnp/train0.tab
      52   49032  230777 outputs/testsnp/train1.tab
      52   49032  230087 outputs/testsnp/train2.tab
     438  398626 1867855 total
$ 
```

Large portions of the command output above, indicated by [...], were ommited for sake of clarity.

As one can see, _FoldSelectSNPs_ received one dataset as input and produced three train-test pairs of datasets in the output dir, for the command used three validation folds as determined in the property file. (There is also a file named _base\_dataset.tab_ , which is a "union" of the folds, containing all samples and all features used in them. The purpose of that dataset is beyond this topic and is discussed in context of the _SNPUtilityComputer_ command.)

Also, by cheking numbers of lines one can see that, although the input dataset _geneconverted\_affy.snp_ contained 1052 SNPs, the output train-test datasets all contain only 50 SNPs. Feature selection when using _FoldSelectSNPs_ is mandatory, and in this case the default number of 50 features was used. Mandatory feature selection was left because often SNP datasets will be very large, with hundreds of thousands of SNPs, and in practice working it is possible to work using only a tiny subset of the total of features.

The selection of the features composing that tiny subset is made by a method exploring a biological bias. There is a well-known tendency in genetics for hereditary conditions to be transmitted by homozigosys of specific alleles. Therefore, when analysing a given feature, the selector assumes that samples will be in the case (or target) category if the feature is homozygotic, control otherwise. The features selected for each fold are then those with highest accuracy according to that classification on the training set.

Now, the numeric conversion. By checking one of the datasets in the output dir, one can see that the data is numeric, following a peculiar convention, as shown below:

```
                                                 MAYO_10249 MAYO_10278 MAYO_10367 ...
                                                 1          2          2          ...
SNP_A-1811033 00->0.0;33->0.75;11->0.25;31->0.5; 0.25       0.25       0.25       ...
SNP_A-1840689 00->0.0;22->0.25;44->0.75;42->0.5; 0.25       0.5        0.25       ...
SNP_A-1881124 24->0.5;00->0.0;44->0.25;          0.25       0.25       0.25       ...
.
.
.
```

(Columns shown by alignment/identation above, for sake of visualization, are actually tab-separated in the file.) As the data excerpt above suggests, the only values allowed in a numeric dataset produced by _FoldSelectSNPs_ are 0.0, 0.25, 0.5 and 0.75 . 0.0 values are by convention assigned to "no-calls", that  is, undetermined SNP values. Also by convention, value 0.5 is always assigned to the heterozygotic pair of the corresponding SNP. Finally, values 0.25 and 0.75 are assigned to the two homozygotic pairs. The association of the actual symbol combinations used in the original SNP dataset is registered at the description field of each SNP - for instance the "00->0.0;33->0.75;11->0.25;31->0.5;" for the first SNP (SNP\_A-1811033) in the excerpt above. (Admitedly that is not a very "orthogonal" or elegant use of the description field, but it avoids the creation of a new file just to record the SNP-to-number mapping used.)