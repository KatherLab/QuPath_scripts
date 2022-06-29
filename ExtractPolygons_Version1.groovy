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

// QuPath version 0.1.2

selectAnnotations();

def imageData = QPEx.getCurrentImageData()
def hierarchy = imageData.getHierarchy()
def annotations = hierarchy.getFlattenedObjectList(null).findAll {it.isAnnotation()}
def server = imageData.getServer()
def name=server.path.minus(".svs").plus(".csv")

def ann_path=buildFilePath(name) 
def ann_file = new File(ann_path) 
ann_file.text = '' 
for (obj in annotations) {
    
    if (obj.isAnnotation()) {
        def roi = obj.getROI()
       // Ignore empty annotations
        if (roi == null) {
            continue
        }
        ann_file<<"X_base, Y_base"<< System.lineSeparator() 
        
        points = roi.getPolygonPoints()
        for (point in points) {
                p_x = point.getX()
                p_y = point.getY()
                point_string = p_x + ", " + p_y
                ann_file << point_string << System.lineSeparator()
            }                     
        }     
}
print("Finished!")
