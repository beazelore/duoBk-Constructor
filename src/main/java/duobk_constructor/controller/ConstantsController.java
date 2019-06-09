package duobk_constructor.controller;

import duobk_constructor.service.ConstantsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/constants")
public class ConstantsController {
    @Autowired
    ConstantsService service;
    @GetMapping(value = "/translation_script")
    public String getTranslationService(){
        return service.getAllConstants().getTranslationScript();
    }
}
