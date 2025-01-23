package org.example;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class MainTest {

    @Test
    public void checkParamersInStringTest(){
        float f = 50;
        String fileContent = "192.168.32.181 - - [14/06/2017:16:47:19 +1000] \"PUT /rest/v1.4/documents?zone=default&_rid=ffc732ea HTTP/1.1\" 200 2 66.222772 \"-\" \"@list-item-updater\" prio:0\n";
        boolean result = App.checkParamersInString(f,  fileContent);
        Assertions.assertTrue(result);

        float f2 = 70;
        boolean result2 = App.checkParamersInString(f2,  fileContent);
        Assertions.assertFalse(result2);
    }
}
