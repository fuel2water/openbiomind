The ViewClusters command produces a raster, graphic image showing a given clustering resullt. The command syntax is:

```
java task.ViewClusters <-d clustering dataset> <-r clustering result> <-o image file> [-clusteringColors traditional|mono]
```

, where:

  * _clustering dataset_ was the one used for producing _clustering results_ . Basically, ViewClusters uses information from _clustering dataset_  to describe features and translate their intensities into colors, and information from _clustering result_  to sectionate clusters adequately.
  * _image file_ will be the PNG file displaying the clustering image.
  * _-clusteringColors_ defines the mapping from intensity to color used. The default value, _traditional_, maps high intensities to red and low intensities to green, with middle-range intensities resulting in dark, near-black tones. The _mono_ value maps high intensities to red and low intensities to black. Image cells in vertical and horizontal clusterings will show gene expression, and so the _tradicional_ scheme is more indicated for that case, since it is a kind of standard among biologists for gene expression clustering. On the other hand, for MUTIC and MOBRA clusterings, where the values depicted are **not** gene expression, the _mono_ scheme is more advised.

Here is a usage example of this command:

```
java task.ViewClusters -d datafiles/horizontal.126.tab -r outputs/horizontal.txt -o outputs/test.png
```

And here is the PNG image produced by that:

![http://img404.imageshack.us/img404/3633/testmg1.png](http://img404.imageshack.us/img404/3633/testmg1.png)

Some comments about the overall structure of the image:

  * As one can see, the style of this image output is very similar to that of many packages for clustering visualization traditionally used in Bioinformatics. However, the version of Omniclust used by OpenBiomind is not hierarchical, and therefore there is no dendrogram at the side of the big colorful rectangle. Instead, the clusters - all in the same level - are separated by blank lines.
  * Also, clusters are in decreasing order geometric quality (homogeneity x separation), and that explains the high uniformity of rows in the clusters at the top.
  * Finally, for each row there is a text in very tiny characters showing the feature (genes, in the example) and their descriptions, as extracted from the clustering dataset. Columns (samples, in the example) are also identified by their dataset ids, but are written vertically, which may produce a confusing layout if the col names are very similar, as is the case in the example.

-- Main.LucioSouza - 16 Oct 2007