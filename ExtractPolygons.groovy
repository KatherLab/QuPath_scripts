import qupath.lib.images.servers.ImageServer
import qupath.lib.objects.PathObject
import qupath.lib.regions.RegionRequest
import qupath.lib.io.PathIO
import javax.imageio.ImageIO
import java.awt.Color
import java.awt.image.BufferedImage
import java.awt.geom.AffineTransform
import java.awt.image.AffineTransformOp
import java.awt.image.DataBufferByte

selectAnnotations();

def imageData = getCurrentImageData()
def hierarchy = imageData.getHierarchy()
def annotations = hierarchy.getFlattenedObjectList(null).findAll {it.isAnnotation()}

def dirOutput = buildFilePath(PROJECT_BASE_DIR, 'export')
mkdirs(dirOutput)

def name= GeneralTools.getNameWithoutExtension(imageData.getServer().getMetadata().getName())
def file = buildFilePath(dirOutput, name.plus(".csv"))
def ann_file = new File(file)


for (obj in annotations) {
    if (obj.isAnnotation()) {
        def roi = obj.getROI()
       // Ignore empty annotations
        if (roi == null) {
            continue
        }
        ann_file << "X_base, Y_base" << System.lineSeparator() 
        points = roi.getAllPoints() //getPolygonPoints()
        // https://forum.image.sc/t/extracting-co-ordinates-doesnt-work-in-newer-qupath-versions/34550
        for (point in points) {
                p_x = point.getX()
                p_y = point.getY()
                point_string = p_x + ", " + p_y
                ann_file << point_string << System.lineSeparator()
            }                     
        }     
}

print("Finished!")

