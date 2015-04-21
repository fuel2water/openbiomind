The _Clusterize_ command operates on clustering data (like that produced by MUTIC and MOBRA transformations), grouping features in that data accordingly to their similarity. Currently, the only clustering algorithm effectively implemented is Omniclust, and therefore there is no parametrization yet for clustering method to be used. The present syntax of this command is:

```
java task.Clusterize <-d clustering dataset> <-o output file> [-datasetClusteringMetric Cosine|Euclidean|SNP]
```

_similarity metric_ can assume the values _Euclidean_ and _Cosine_ (used for gene expression data) as well as SNP (specially designed for dealing with SNP discrete data). If this option is not specified, the default value _Euclidean_ defined in _pipeline.properties_ is assumed. And here is an utilization example of the command:

```
$ java -cp .:openbiomind-bin_0.60.jar task.Clusterize -d datafiles/horizontal126.tab -o outputs/horizontal.txt
Clustering element 1 of 571: Cnih2->Mapk7 (0.008131241)
Clustering element 2 of 571: Cpne6->Trove2 (0.0038395962)
Clustering element 3 of 571: Ehf->Hmx1 (0.019612424)
Clustering element 4 of 571: Oprd1->Gpr37 (0.007492103)
Clustering element 5 of 571: C77370->Stil (0.012123372)
Clustering element 6 of 571: Selp->S100a9 (0.00470515)
Clustering element 7 of 571: Indo->Tcte2 (0.0052084182)
Clustering element 8 of 571: Flt3l->Rmi1 (0.0047482788)
Clustering element 9 of 571: 5430432M24Rik->Hrh1 (0.0072157434)
[...]
Clustering element 567 of 571: Zic2->D530037H12Rik (0.0064240713)
Clustering element 568 of 571: Prss7->Tll1 (0.0068122772)
Clustering element 569 of 571: Hrh1->Gria1 (0.0075570517)
Clustering element 570 of 571: Bre->Alg3 (0.001777195)
Clustering element 571 of 571: Bcar1->Smad6 (0.0020972115)
$ more outputs/horizontal.txt 
Cluster #1
Quality: 0.026617197
Homogeneity: 0.026987657
Separation: 0.986273
Stra6   stimulated by retinoic acid gene 6
Evx1    even skipped homeotic gene 1 homolog

Cluster #2
Quality: 0.025406642
Homogeneity: 0.025841149
Separation: 0.98318547
Art2a   ADP-ribosyltransferase 2a
Timm8a2 translocase of inner mitochondrial membrane 8 homolog a2 (yeast)
D13Ertd37e      DNA segment, Chr 13, ERATO Doi 37, expressed
Mog     myelin oligodendrocyte glycoprotein
Cyp2j6  cytochrome P450, family 2, subfamily j, polypeptide 6
Svp2    seminal vesicle protein 2
Traf1   Tnf receptor-associated factor 1
AA517545        expressed sequence AA517545
Foxg1   forkhead box G1
Anxa10  annexin A10
Mybl1   myeloblastosis oncogene-like 1
C78878  expressed sequence C78878
Klk1b3  kallikrein 1-related peptidase b3
Obp1a   odorant binding protein Ia
Gsg2    germ cell-specific gene 2
Melk    maternal embryonic leucine zipper kinase
Stx1b2  syntaxin 1B2///syntaxin 1B1

Cluster #3
$ 
```

As one can see, clustering output is presented in decreasing order of "geometric quality". (The product homogeneity times separation, which are geometric measurements of how compacted is a cluster and how far from neighbors it is.) Also, so far clustering is not hierarchical, for all clustering analysis currently performed as part of the research work at Biomind are concerned only with the first level of clusters. (The one shown in the output above.)