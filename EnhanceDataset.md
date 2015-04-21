This command is in fact a kind of relatively sophisticated dataset transformation, based on ontological data know about genes. For the purposes of OpenBiomind, an ontology is a set of gene categories defined by some aspect of biological relevance. As examples, GO (Gene Ontology) is a set of _functional_ gene categories, while PIR (Protein Information Resource) is a set of gene categories defined by protein types.

The syntax of this command is:

```
java task.EnhanceDataset <-d original dataset> <-e enhanced dataset> [-ontologyDescriptionFile ontology description file] [-ontologyAssociationFile ontology association file]
```

If not supplied, the optional arguments _description file_ and _ontology description file_, the default values stored at _pipeline.properties_ are used instead.

_description file_ is a file mapping ontogy category ids to their descriptions. It is a simple tabular list of two columns in the form

```
category 1 \t description 1
category 2 \t description 2
category 3 \t description 3
.
.
.
category n \t description n
```

In the example below, one can see the appearance of such a file for Gene Ontology:

```
$ head datafiles/go/go2desc.txt 
GO:0000001      mitochondrion inheritance; biological_process
GO:0000002      mitochondrial genome maintenance; biological_process
GO:0000003      reproduction; biological_process
GO:0000005      ribosomal chaperone activity; molecular_function
GO:0000006      high affinity zinc uptake transmembrane transporter activity; molecular_function
GO:0000007      low-affinity zinc ion transmembrane transporter activity; molecular_function
GO:0000008      thioredoxin; molecular_function
GO:0000009      alpha-1,6-mannosyltransferase activity; molecular_function
GO:0000010      trans-hexaprenyltranstransferase activity; molecular_function
GO:0000011      vacuole inheritance; biological_process
$ 
```

As for the association file, it is again a simple tabular file of two columns, but this time in the form

```
feature 1 \t category 23
feature 2 \t category 257
feature 3 \t category 42
```

Here is an example mapping gene features (id-ed using gene symbols) to GO categories:

```
$ head datafiles/go/gene2go.mouse.txt 
0610006I08Rik   GO:0016021
0610007C21Rik   GO:0016020
0610007C21Rik   GO:0016021
0610007L01Rik   GO:0016021
0610007L01Rik   GO:0016020
0610007P08Rik   GO:0004386
0610007P08Rik   GO:0003676
0610007P08Rik   GO:0008026
0610007P08Rik   GO:0003677
0610007P14Rik   GO:0016126
$
```

Finally, in the example below the enhancement of a dataset with GO features is shown, stressing the addition of over a thousand features and exemplifying some of the newly added feature lines in the enhanced file.

```
$ wc datafiles/varm126.tab 
     573   22117  170516 datafiles/varm126.tab
$ java task.EnhanceDataset -d datafiles/varm126.tab -e datafiles/ext.varm126.tab
$ wc datafiles/ext.varm126.tab 
    1664   64023  494241 datafiles/ext.go.varm126.tab
$ grep "GO:" datafiles/ext.varm126.tab | head
GO:0000030      mannosyltransferase activity; molecular_function        540.1   558.0   580.2   676.4   677.95  719.95  600.7   591.55  506.5   493.65  611.0   769.75  549.6   761.1   623.85  521.35  699.95  613.3    669.0   648.95  578.85  580.75  637.6   691.66785       598.3046        533.3996        556.27826       640.6726        537.3458        476.97006       475.8612        508.065 648.366
GO:0000050      urea cycle; biological_process  10.05   60.1    46.65   51.55   36.95   38.7    27.05   47.15   106.65  13.05   68.8    14.15   23.05   26.4    45.95   19.05   81.15   34.5    49.65   19.95   29.95    14.1    24.25   27.682724       34.028915       25.283682       63.328503       41.526787       41.439713       24.128979       31.004213       46.503902       34.448967
GO:0000060      protein import into nucleus, translocation; biological_process  175.25  161.3   176.65  149.45  231.1   146.15  153.8   221.3   244.85  160.35  237.4   123.7   199.25  145.5   162.0   188.35  207.4    255.9   306.45  189.25  245.95  122.55  106.85  243.30685       149.9688        245.29037       155.42085       184.80516       206.89832       159.95662       122.69792       203.90463       227.02896
GO:0000074      regulation of progression through cell cycle; biological_process        237.05  253.09999       231.9875        228.0625        259.725 272.75  268.72504       234.9875        231.98752       241.35   257.0   311.78748       116.45  293.0875        252.3   235.15  241.25  218.8875        218.2875        304.4875        268.03748       279.575 278.875 203.61206       194.94221       215.6285        245.88902        184.73462       334.41995       300.22885       307.00858       272.96225       275.899
GO:0000079      regulation of cyclin-dependent protein kinase activity; biological_process      73.1    131.1   56.0    47.4    37.2    98.6    131.5   74.7    54.3    76.3    24.7    73.0    50.1    22.7    67.9     60.4    63.4    59.8    52.4    94.7    71.6    97.4    122.0   59.50948        88.980385       84.355064       90.462204       74.16556        72.543396       93.44373        11.158907       120.318504       113.03226
GO:0000082      G1/S transition of mitotic cell cycle; biological_process       113.8   102.6   75.9    117.1   123.5   81.1    97.1    72.6    91.1    81.5    112.0   123.1   60.9    96.5    92.7    66.6    109.8    76.5    121.0   113.0   99.4    80.5    155.8   158.98116       13.344421       31.15891        94.54235        76.02868        140.5646        145.0303        113.82997       202.03845       51.334866
GO:0000089      mitotic metaphase; biological_process   169.9   132.8   127.2   149.0   110.4   171.5   153.3   147.6   163.2   173.4   123.3   213.2   110.6   176.4   196.9   119.6   138.1   176.6   191.9   163.3    218.0   177.9   188.0   150.84421       145.63023       160.03955       138.88669       151.10605       206.23158       157.85756       195.55843       231.35013       205.70619
GO:0000109      nucleotide-excision repair complex; cellular_component  62.8    66.1    69.4    67.6    85.8    108.5   59.6    55.5    55.3    135.2   69.3    64.6    83.6    126.0   74.1    63.6    79.3    58.6     68.2    154.7   52.6    35.2    72.9    76.18423        58.32618        59.819023       85.587326       104.06884       72.61679        138.3706        92.516846       85.13379        95.568756
GO:0000120      RNA polymerase I transcription factor complex; cellular_component       107.6   25.6    51.0    53.0    34.6    122.6   148.6   135.8   155.9   114.9   188.8   207.0   223.4   141.5   182.1   96.1     87.0    55.9    83.7    108.8   127.5   58.8    148.8   155.28555       135.82635       157.54523       142.71095       111.32479       163.88126       159.95337       161.61423       139.7066        163.59157
GO:0000122      negative regulation of transcription from RNA polymerase II promoter; biological_process        291.67142       241.20715       234.8   291.99286       217.54286       298.52142       280.1642366.62857        303.16428       292.15714       299.1214        188.11427       314.07144       209.67857       285.06427       371.44995       292.15  370.53574       280.79285       321.81427       261.1928308.7857 247.40001       265.36502       237.7703        238.73874       311.074 295.85318       191.85384       239.86948       261.09152       245.91434       239.17839
$ 
```

# Current Ontology Files #

The downloadable extras package in this project currently has two examples of ontologic mappings at _datafiles/go_ and _datafiles/pir_:

  * _datafiles/go_ contains the description file _go2desc.txt_ for the whole Gene Ontology, while the _gene2go.mouse.txt_ file in the same directory contains all gene symbol-to-GO associations for mice. Currently those are the default ontologyy files assigned in the _pipeline.properties_ system file.
  * _datafiles/pir_ contains the description file  _pir2desc.txt_ for the whole Protein Information Resource ontology. In the same directory, _gene2pir.varm126.txt_ is a small customized association file containing the gene symbol-to-PIR mappings for the dataset _varm126.tab_ alone.

Anyhow, the extremely simple ontology data format outlined above makes it relatively easy for a user to script any other ontological set for use in OpenBiomind.