/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.miroslavbartyzal.directoryfootprint;

import java.io.File;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.nio.file.Path;

/**
 *
 * @author Miroslav Bartyzal (miroslavbartyzal@gmail.com)
 */
@XmlAccessorType(XmlAccessType.NONE)
public class DirTreeNode
{

    @XmlElement(name = "path")
    @XmlJavaTypeAdapter(PathAdapter.class)
    public Path path;
    
    @XmlElement(name = "directory")
    public boolean directory;
    
    @XmlElement(name = "size")
    public Long size;
    
    @XmlElement(name = "md5")
    public String md5;
    
    @XmlElement(name = "zippath")
    @XmlJavaTypeAdapter(PathAdapter.class)
    public Path zippath;
    
    @XmlElement(name = "zipsize")
    public Long zipsize;
    
    @XmlElement(name = "zipmd5")
    public String zipmd5;

}
