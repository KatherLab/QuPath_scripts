// contributors: JN Kather, P Bankhead, J Krause
// license: MIT license: https://opensource.org/licenses/MIT
// this is for QuPath v0.2.0
// this script will visualize a deep learning prediction map

// Define data file, delimiter & tile size (in pixels)
import qupath.lib.roi.ROIs
import qupath.lib.objects.PathObjects
import qupath.lib.regions.ImagePlane



// Define data file, delimiter & tile size (in pixels)


// PROSTATE - example image is Prostate EJ-7317 
def path = 'I:/PT1PT2-CRCFULL-DX/DUMP/ARIYHQWAIFCT_macenko-VAMPK_LN_MET_lastResult_v6.mat-LN_MET-blockLevelPredictions.csv'
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
def name = server.getMetadata().getName()
if (name.endsWith('.scn'))
    name = name.substring(0, name.length()-4)

// Find the relevant lines containing the image name
def lines = new File(path).readLines()
def header = lines.remove(0).split(delim)
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
    def roi = ROIs.createRectangleROI(x, y, tileWidth, tileHeight, ImagePlane.getDefaultPlane())
    def tile = PathObjects.createTileObject(roi)
    def ml = tile.getMeasurementList()
    for (int i = 1; i < header.size(); i++) {
        ml.putMeasurement(header[i], split[i] as double)
    }
    ml.close()
    tiles.add(tile)
}


// Remove any existing objects & add the tiles
clearAllObjects()
addObjects(tiles)
print 'Tiles added for ' + name + ':\t' + tiles.size()

// set extreme colors
def min = PathObjects.createDetectionObject(ROIs.createEmptyROI())
min.getMeasurementList().putMeasurement('MSIH', 0)
min.getMeasurementList().putMeasurement('nonMSIH', 0)

def max = PathObjects.createDetectionObject(ROIs.createEmptyROI())
max.getMeasurementList().putMeasurement('MSIH', 1)
max.getMeasurementList().putMeasurement('nonMSIH', 1)

addObjects([min, max])
fireHierarchyUpdate()

def toRemove = getDetectionObjects().findAll {it.getROI().isEmpty()}
removeObjects(toRemove, true)
