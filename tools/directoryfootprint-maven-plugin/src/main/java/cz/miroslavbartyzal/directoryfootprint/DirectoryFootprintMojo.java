package cz.miroslavbartyzal.directoryfootprint;

/*
 * Copyright 2001-2005 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

@Mojo(name = "dirfootprint")
public class DirectoryFootprintMojo extends AbstractMojo
{

    @Parameter(required = true)
    private File outputFile;

    @Parameter(alias = "inputDirectory", required = true)
    private String inputDirectoryString;

    @Parameter(required = true)
    private String version;

    @Parameter(required = false)
    private boolean zipIt;

    @Parameter(required = false)
    private String zipPassword;

    @Parameter(defaultValue = "zip", required = false)
    private String zipExtension;

    @Override
    public void execute() throws MojoExecutionException
    {
        Long time = System.currentTimeMillis();
        Path inputDirectory = Paths.get(inputDirectoryString);
        DirTree dirTree = null;
        super.getLog().info("Working on " + inputDirectory.toAbsolutePath());
        try {
            if (zipIt) {
                dirTree = new DirTree(inputDirectory, version, zipExtension, zipPassword);
            } else {
                dirTree = new DirTree(inputDirectory, version);
            }
        } catch (Exception ex) {
            super.getLog().error(ex);
        }

        JAXBContext jAXBContext;
        Marshaller jAXBmarshaller;
        try {
            jAXBContext = JAXBContext.newInstance(DirTree.class, DirTreeNode.class);
            jAXBmarshaller = jAXBContext.createMarshaller();
            jAXBmarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            jAXBmarshaller.marshal(dirTree, outputFile);
            super.getLog().info("Directory footprint saved to " + outputFile.getAbsolutePath());
        } catch (JAXBException ex) {
            super.getLog().error(ex);
        }
        super.getLog().info("Finished in " + (System.currentTimeMillis() - time) / 1000 + " seconds.");
    }

}
