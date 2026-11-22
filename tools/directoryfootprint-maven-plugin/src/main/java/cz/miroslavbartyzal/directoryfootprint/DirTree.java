/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.miroslavbartyzal.directoryfootprint;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import net.lingala.zip4j.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.model.ZipParameters;
import net.lingala.zip4j.model.enums.CompressionLevel;
import net.lingala.zip4j.model.enums.CompressionMethod;
import net.lingala.zip4j.model.enums.EncryptionMethod;

/**
 *
 * @author Miroslav Bartyzal (miroslavbartyzal@gmail.com)
 */
@XmlRootElement(name = "dirtree")
@XmlAccessorType(XmlAccessType.NONE)
public class DirTree
{

    @XmlAttribute(name = "version")
    private String version;
    @XmlElement(name = "file")
    private List<DirTreeNode> dirNodes;
    private final boolean zipIt;
    private final String zipExtension;
    private final String zipPassword;
    private final Path superParentDirectory;

    private DirTree()
    {
        zipIt = false;
        zipExtension = null;
        zipPassword = null;
        superParentDirectory = null;
    }

    public DirTree(Path parentDirectory, String version) throws Exception
    {
        this(parentDirectory, version, null, null);
    }

    public DirTree(Path parentDirectory, String version, String zipExtension,
            String zipPassword) throws Exception
    {
        if (!Files.isDirectory(parentDirectory)) {
            throw new Exception(
                    "Given file \"" + parentDirectory.toAbsolutePath() + "\" is not a directory!");
        }

        this.superParentDirectory = parentDirectory;
        this.version = version;
        this.zipExtension = zipExtension;
        this.zipPassword = zipPassword;
        zipIt = zipExtension != null;
        dirNodes = new ArrayList<>();
        mapDirTree(parentDirectory);
    }

    private void mapDirTree(Path parentDirectory) throws IOException
    {
        for (Path f : Files.list(parentDirectory).collect(Collectors.toSet())) {
            DirTreeNode dirTreeNode = new DirTreeNode();
            dirTreeNode.path = superParentDirectory.relativize(f);
            if (!Files.isDirectory(f)) {
                dirTreeNode.directory = false;
                dirTreeNode.size = Files.size(f);
                dirTreeNode.md5 = FileChecksum.getMD5Checksum(f);
                if (zipIt) {
                    Path zippedFile = zipIt(f);
                    dirTreeNode.zippath = superParentDirectory.relativize(zippedFile);
                    dirTreeNode.zipsize = Files.size(zippedFile);
                    dirTreeNode.zipmd5 = FileChecksum.getMD5Checksum(zippedFile);
                    Files.deleteIfExists(f);
                }
                dirNodes.add(dirTreeNode);
            } else {
                dirTreeNode.directory = true;
                dirNodes.add(dirTreeNode);
                mapDirTree(f);
            }
        }
    }

    private Path zipIt(Path fileToZip)
    {
        try {
            // Initiate ZipFile object with the path/name of the zip file.
            ZipFile zipFile = new ZipFile(fileToZip.toAbsolutePath() + "." + zipExtension, zipPassword.toCharArray());

            // Initiate Zip Parameters which define various properties such
            // as compression method, etc. More parameters are explained in other
            // examples
            ZipParameters parameters = new ZipParameters();
            parameters.setCompressionMethod(CompressionMethod.DEFLATE);
            parameters.setCompressionLevel(CompressionLevel.ULTRA);

            if (zipPassword != null && !zipPassword.isEmpty()) {
                // Set the encryption flag to true
                // If this is set to false, then the rest of encryption properties are ignored
                parameters.setEncryptFiles(true);

                // Set the encryption method to Standard Zip Encryption
                parameters.setEncryptionMethod(EncryptionMethod.ZIP_STANDARD);
            }

            // Now add files to the zip file
            // Note: To add a single file, the method addFile can be used
            // Note: If the zip file already exists and if this zip file is a split file
            // then this method throws an exception as Zip Format Specification does not
            // allow updating split zip files
            zipFile.addFile(fileToZip.toString(), parameters);

            return zipFile.getFile().toPath();
        } catch (ZipException ex) {
            ex.printStackTrace(System.err);
        }
        return null;
    }

    public String getVersion()
    {
        return version;
    }

}
