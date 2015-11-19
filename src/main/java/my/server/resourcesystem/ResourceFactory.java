package my.server.resourcesystem;

import my.server.base.Resource;
import my.server.base.VFS;
import my.server.resourcesystem.sax.ReadXMLFileSAX;
import my.server.utils.VFSImpl;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ResourceFactory {

    private static ResourceFactory instance;

    private Map<String, Resource> resources = new ConcurrentHashMap<>();

    private ResourceFactory() {
        VFS vfs = new VFSImpl("");
        Iterator<String> iterator = vfs.getIterator("./data/");
        while (iterator.hasNext()) {
            String resourceFileXML = iterator.next();
            if (!vfs.isDirectory(resourceFileXML)) {
                resources.put(resourceFileXML, (Resource) ReadXMLFileSAX.readXML(resourceFileXML));
            }
        }
    }

    public static ResourceFactory instance() {
        if (instance == null) {
            instance = new ResourceFactory();
        }
        return instance;
    }

    public Resource getResource(String path) {
        return resources.get(path);
    }
}
