package com.presbo.presboservice.utility;

import net.sf.mpxj.MPXJException;
import net.sf.mpxj.ProjectFile;
import net.sf.mpxj.reader.ProjectReader;
import net.sf.mpxj.reader.UniversalProjectReader;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public class MpxjUtility {

    public static ProjectFile readProjectFile (MultipartFile file) throws MPXJException, IOException {

        ProjectReader projectReader = new UniversalProjectReader();

        return projectReader.read(file.getInputStream());

    }

}
