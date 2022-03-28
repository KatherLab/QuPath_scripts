// contributors: JN Kather, P Bankhead
// license: MIT license: https://opensource.org/licenses/MIT
// this is for QuPath v0.2.0
// this script will visualize a deep learning prediction map

// Define data file, delimiter & tile size (in pixels)
import qupath.lib.objects.*
import qupath.lib.roi.*
import qupath.lib.objects.PathDetectionObject
import qupath.lib.roi.RectangleROI
import qupath.lib.images.servers.ImageServer
import qupath.lib.objects.PathObject
import qupath.lib.regions.RegionRequest
import qupath.lib.roi.PathROIToolsAwt
import qupath.lib.scripting.QPEx
import javax.imageio.ImageIO
import java.awt.Color
import java.awt.image.BufferedImage
import java.awt.geom.AffineTransform
import java.awt.image.AffineTransformOp
import java.awt.image.DataBufferByte
import java.awt.Rectangle
import java.awt.geom.Area
import qupath.lib.regions.* 
import ij.* 
import java.awt.Color 
import java.awt.image.BufferedImage 
import javax.imageio.ImageIO
import qupath.lib.common.ColorTools
import qupath.lib.objects.classes.PathClass
import qupath.lib.regions.RegionRequest
import qupath.lib.roi.PathROIToolsAwt
import qupath.lib.scripting.QPEx
import qupath.lib.roi.RectangleROI
import qupath.lib.objects.PathAnnotationObject
import javax.imageio.ImageIO
import java.awt.Color
import java.awt.image.BufferedImage
import java.awt.image.DataBufferByte
import java.awt.image.IndexColorModel
// Define data file, delimiter & tile size (in pixels)


// PROSTATE - example image is Prostate EJ-7317 
def path = 'I:/PT1PT2-CRCFULL-DX/DUMP/ARIYHQWAIFCT_macenko-PLDIP_isMSIH_lastResult_v6.mat-isMSIH-blockLevelPredictions.csv'
//def path = 'E:/YORKSHIRE-RESECTIONS-DX/DUMP/ALNQMKDDEAFQ-FRIVI_isMSIH_lastResult_v6.mat-isMSIH-blockLevelPredictions.csv'


// BREAST - example image is  Breast A7-A13G
//def path = 'E:/Mapdata/FTPACHNTLFLD_VRT_PRStatus_lastResult.mat-PRStatus-blockLevelPredictions.csv'


// COLORECTAL - example image is Colon A6-5661
//def path = 'E:/ALLBLOCKS/TCGA-CRC-DX/DUMP/NAGRRAVYERPG_VYS_MSIStatus_lastResult.mat-MSIStatus-blockLevelPredictions.csv'
//def path = 'E:/ALLBLOCKS/TCGA-CRC-DX/DUMP/NAGRRAVYERPG_RII_Hypermutated_lastResult.mat-Hypermutated-blockLevelPredictions.csv'


def delim = ';'
def tileWidth = 512 // Should this be 512? Depends on export resolution
def tileHeight = 512

// Get the current image data
def imageData = getCurrentImageData()

// Get the image name
def server = imageData.getServer()
def name = server.getShortServerName()
if (name.endsWith('.scn'))
    name = name.substring(0, name.length()-4)
    
hierarchy = getCurrentHierarchy()
// Find the relevant lines containing the image name
def lines = new File(path).readLines()
def header = lines.remove(0).split(delim)
print(header)
// Parse the tile location

def tiles = []
for (line in lines.findAll{it.contains(name)}) {
    // Parse coordinates
    def split = line.split(delim)
    def coordsString = split[0].substring(split[0].lastIndexOf('_(')+2, split[0].lastIndexOf(').'))
    def coords = coordsString.split(',')
    def x = coords[0] as int
    def y = coords[1] as int
    // Create objects
    def roi = new RectangleROI(x, y, tileWidth, tileHeight)   
    msi = split[1]
    if ('0' < msi && msi <= '0.2')  
        {def rgb = getColorRGB(0,0, 204)           
         def tile = new PathAnnotationObject(roi)
         tile.setColorRGB(rgb)
         tile.setName('MSI_0-0,2')
         imageData.getHierarchy().addPathObject(tile, false) }
    else if ('0.2' < msi && msi <= '0.4')
        {def rgb = getColorRGB(51,153,255)           
         def tile = new PathAnnotationObject(roi)
         tile.setColorRGB(rgb)
         tile.setName('MSI_0,2-0,4')
         imageData.getHierarchy().addPathObject(tile, false) }
    else if ('0.4' < msi && msi <= '0.6')
        {def rgb = getColorRGB(255,255,255)           
         def tile = new PathAnnotationObject(roi)
         tile.setColorRGB(rgb)
         tile.setName('MSI_0,4-0,6')
         imageData.getHierarchy().addPathObject(tile, false) }         
    else if ('0.6' < msi && msi <= '0.8') 
        {def rgb = getColorRGB(255,153,153)           
         def tile = new PathAnnotationObject(roi)
         tile.setColorRGB(rgb)
         tile.setName('MSI_0,6-0,8')
         imageData.getHierarchy().addPathObject(tile, false) }             
    else
        {def rgb = getColorRGB(255,0,0)
         def tile = new PathAnnotationObject(roi)
         tile.setColorRGB(rgb)
         tile.setName('MSI_0,8-1')
         imageData.getHierarchy().addPathObject(tile, false)  }

  
}


