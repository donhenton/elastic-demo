package com.dhenton9000.elastic.demo.support;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.web.servlet.error.AbstractErrorController;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

/*
this may only be needed for handling errors for serving web pages
the ControllerAdvice may be for REST stuff

*/
@Controller
public class CustomErrorController extends AbstractErrorController {

    private static final String ERROR_PATH = "/error";
 

    @Autowired
     public CustomErrorController(ErrorAttributes errorAttributes) {
         super(errorAttributes);
     }

    @Override
    public String getErrorPath() {
        return ERROR_PATH;
    }
/*
    not needed as the exceptioncontrolleradvice intercepts
    
    @RequestMapping("/error")
    @ResponseBody
    public String handleError(HttpServletRequest request) {
        
        Map<String, Object> errorAttributesItems =
        this.getErrorAttributes(request, true);
       
        final StringBuilder errorDetails = new StringBuilder();
        errorAttributesItems.forEach((attribute, value) -> {
            errorDetails.append("<tr><td>")
                    .append(attribute)
                    .append("</td><td><pre>")
                    .append(value)
                    .append("</pre></td></tr>");
        });

        return String.format("<html><head><style>td{vertical-align:top;border:solid 1px #666;}</style>"
                + "</head><body><h2>Error Page</h2><table>%s</table></body></html>", errorDetails.toString());
    }
*/
    @RequestMapping("/404")
    public String pageNotFound(Model model, HttpServletRequest request) {
        Map<String, Object> items = getErrorAttributes(request, true);
        model.addAttribute("error", items);
        return "404";
    }

    

}
