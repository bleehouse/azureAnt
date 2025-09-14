package org.citybot.ant;

import java.io.FileOutputStream;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;

import com.microsoft.windowsazure.services.blob.client.CloudBlobClient;
import com.microsoft.windowsazure.services.blob.client.CloudBlobContainer;
import com.microsoft.windowsazure.services.blob.client.CloudBlockBlob;
import com.microsoft.windowsazure.services.core.storage.CloudStorageAccount;

public class AzureBlobFileDownload extends Task {
	String blob;
	String container;
	String account;
	String key;
	String protocol = "http";
	String file;
	String notfound = "continue";
	public void setBlob(String blob) {
		this.blob = blob;
	}
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
	public void setFile(String file) {
		this.file = file;
	}
	public void setNotFound(String notfound) {
		this.notfound = notfound;
	}
	public void execute() {
        if (blob==null) {
            throw new BuildException("Property 'blob' is required");
        }
        if (container==null) {
            throw new BuildException("Property 'container' is required");
        }
        if (account==null) {
            throw new BuildException("Property 'account' is required");
        }
        if (key==null) {
            throw new BuildException("Property 'key' is required");
        }
        if(file == null) {
        	throw new BuildException("Property 'file' is required");
        }
        try {
        	var storageConnectionString = String.format("DefaultEndpointsProtocol=%s;AccountName=%s;AccountKey=%s", protocol, account, key);
			var storageAccount = CloudStorageAccount.parse(storageConnectionString);
			var blobClient = storageAccount.createCloudBlobClient();
			var blobContainer = blobClient.getContainerReference(container);

			// Create or overwrite the "myimage.jpg" blob with contents from a local file
			CloudBlockBlob blobHandle = blobContainer.getBlockBlobReference(blob);
			if(blobHandle == null || !blobHandle.exists()) {
				switch (notfound.toLowerCase()) {
					case "fail" -> {
						getProject().log("Cannot find blob " + blob);
						throw new BuildException("Cannot find blob " + blob);
					}
					case "continue" -> {}
					case "log" -> getProject().log("Cannot find blob " + blob);
					default -> {}
				}
			}
			getProject().log("Downloading blob " + blob + " to " + file);
			blobHandle.download(new FileOutputStream(file));
		} catch (Exception e) {
			switch (notfound.toLowerCase()) {
				case "fail" -> throw new BuildException(e);
				case "continue" -> {}
				case "log" -> getProject().log("Cannot find blob " + blob);
				default -> {}
			}
		} 
    }
	
}
