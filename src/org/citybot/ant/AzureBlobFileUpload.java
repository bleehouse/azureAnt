package org.citybot.ant;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.DirectoryScanner;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.types.FileSet;

import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.BlobServiceClientBuilder;
import com.azure.storage.blob.models.BlobContainerProperties;
import com.azure.storage.blob.models.PublicAccessType;

public class AzureBlobFileUpload extends Task {
	String container;
	String account;
	String key;
	String blobpath;
	String protocol = "http";
	private final List<FileSet> filesets = new ArrayList<>();
	private boolean create = true;
	private boolean list = false;
	public void setContainer(String container) {
		this.container = container;
	}
	public void setAccount(String account) {
		this.account = account;
	}
	public void setKey(String key) {
		this.key = key;
	}
	public void setProtocol(String protocol) {
		this.protocol = protocol;
	}
	public void setCreate(boolean create) {
		this.create = create;
	}

	public void setList(boolean list) {
		this.list = list;
	}
	public void addFileset(FileSet fileset) {
        filesets.add(fileset);
    }
	
	public String getBlobpath() {
		return blobpath;
	}
	public void setBlobpath(String blobpath) {
		this.blobpath = blobpath;
	}
	public void execute() {
        if (container==null) {
            throw new BuildException("Property 'container' is required");
        }
        if (account==null) {
            throw new BuildException("Property 'account' is required");
        }
        if (key==null) {
            throw new BuildException("Property 'key' is required");
        }
        if(filesets.isEmpty()) {
        	throw new BuildException("A nested 'fileset' is required");
        }
        try {
        	var connectionString = String.format("DefaultEndpointsProtocol=%s;AccountName=%s;AccountKey=%s", protocol, account, key);
			var blobServiceClient = new BlobServiceClientBuilder()
                .connectionString(connectionString)
                .buildClient();
            
            var containerClient = blobServiceClient.getBlobContainerClient(container);
			if(create && !containerClient.exists()) {
				containerClient = blobServiceClient.createBlobContainer(container);
			}

			// Create or overwrite the "myimage.jpg" blob with contents from a local file
			
			for(FileSet fs : filesets) {
				var ds = fs.getDirectoryScanner(getProject());
	            String[] includedFiles = ds.getIncludedFiles();
		        for(int i=0; i<includedFiles.length; i++) {
		        	String filename = includedFiles[i].replace('\\','/');
		        	File source = new File(ds.getBasedir() + "/" + filename);
		        	String blobname = filename.substring(filename.lastIndexOf("/")+1);
		        	if(getBlobpath()!=null){
		        		blobname = getBlobpath() + "/" + blobname;
		        	}
                    var blobClient = containerClient.getBlobClient(blobname);
                    blobClient.uploadFromFile(source.getAbsolutePath());
                    
                    if(list) {
                        getProject().log("Uploaded blob: " + blobname);
                    }
		        	}
	                CloudBlockBlob blobHandle = blobContainer.getBlockBlobReference(blobname);
	                blobHandle.upload(new FileInputStream(source), source.length());
		        }
			}
			
			if(list) {
				// Loop over blobs within the container and output the URI to each of them
				for (ListBlobItem blobItem : blobContainer.listBlobs()) {
					getProject().log(blobItem.getUri().toString());
				}
			}
		} catch (InvalidKeyException e) {
			throw new BuildException(e);
		} catch (URISyntaxException e) {
			throw new BuildException(e);
		} catch (StorageException e) {
			throw new BuildException(e);
		} catch (FileNotFoundException e) {
			throw new BuildException(e);
		} catch (IOException e) {
			throw new BuildException(e);
		}
    }
	
}
