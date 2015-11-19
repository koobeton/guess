package my.server.utils;

import my.server.base.VFS;

import java.io.File;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;

public class VFSImpl implements VFS {

	private String root;
	
	public VFSImpl(String root) {
		this.root = root;
	}

	public boolean isDirectory(String path) {
		return new File(root + path).isDirectory();
	}

	
	public Iterator<String> getIterator(String startDir) {
		return new FileIterator(startDir);
	}

	private class FileIterator implements Iterator<String> {

		private Queue<File> files = new LinkedList<>();
		
		public FileIterator(String path) {
			files.add(new File(root + path));
		}
		
		public boolean hasNext() {			
			return !files.isEmpty();
		}

		public String next() {
            File file = files.peek();
            if(file.isDirectory()) {
                Collections.addAll(files, file.listFiles());
            }
            return files.poll().getPath().replace(File.separatorChar, '/');
		}

		public void remove() {			
			
		}
	}

	@Override
	public String getAbsolutePath(String file) {
		return new File(root + file).getAbsolutePath();
	}


	@Override
	public boolean isExist(String path) {
		return new File(root + path).exists();
	}


	@Override
	public byte[] getBytes(String file) {
		return FileReaderHelper.readByteFile(getAbsolutePath(file));
	}


	@Override
	public String getUFT8Text(String file) {
        return FileReaderHelper.readTextFile(getAbsolutePath(file), "UTF-8");
    }
}
