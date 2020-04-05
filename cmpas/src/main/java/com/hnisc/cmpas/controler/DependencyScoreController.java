package com.hnisc.cmpas.controler;

import com.hnisc.cmpas.bean.DependencyScore;
import com.hnisc.cmpas.service.IDependencyScoreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;

/**
 * <p>
 * ${table.comment} 前端控制器
 * </p>
 *
 * @author humorchen
 * @since 2019-07-13
 */
@RestController
@RequestMapping("/cmpas/dependencyScore")
public class DependencyScoreController {
    @Autowired
	private IDependencyScoreService iDependencyScoreService;
    protected void addDependencyScore(DependencyScore dependencyScore)
    {
        iDependencyScoreService.insert(dependencyScore);
    }

}
