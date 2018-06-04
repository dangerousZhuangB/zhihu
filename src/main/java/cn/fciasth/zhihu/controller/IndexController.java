package cn.fciasth.zhihu.controller;

import cn.fciasth.zhihu.bean.EntityType;
import cn.fciasth.zhihu.bean.HostHolder;
import cn.fciasth.zhihu.bean.Question;
import cn.fciasth.zhihu.bean.User;
import cn.fciasth.zhihu.service.CommentService;
import cn.fciasth.zhihu.service.FollowService;
import cn.fciasth.zhihu.service.QuestionService;
import cn.fciasth.zhihu.service.UserService;
import cn.fciasth.zhihu.vo.ViewObject;
import com.sun.org.apache.xpath.internal.operations.Mod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.List;

@Controller
public class IndexController {

    private static final Logger logger = LoggerFactory.getLogger(IndexController.class);

    @Autowired
    private UserService userService;

    @Autowired
    private QuestionService questionService;

    @Autowired
    private CommentService commentService;

    @Autowired
    private FollowService followService;

    @Autowired
    private HostHolder hostHolder;

    @RequestMapping(value = {"/user/{userId}"},method = RequestMethod.GET)
    public String userIndex(Model model, @PathVariable("userId") int userId){
        model.addAttribute("vos",getQuestions(userId,0,10));

        User user = userService.getUser(userId);
        ViewObject vo = new ViewObject();
        vo.set("user", user);
        vo.set("commentCount", commentService.getUserCommentCount(userId));
        vo.set("followerCount", followService.getFollowerCount(EntityType.ENTITY_USER, userId));
        vo.set("followeeCount", followService.getFolloweeCount(userId, EntityType.ENTITY_USER));
        if (hostHolder.getUser() != null) {
            vo.set("followed", followService.isFollower(hostHolder.getUser().getId(), EntityType.ENTITY_USER, userId));
        } else {
            vo.set("followed", false);
        }
        model.addAttribute("profileUser", vo);
        return "profile";
//        return "index";
    }

    @RequestMapping(value = {"/","/index"},method = RequestMethod.GET)
    public String index(Model model){
        model.addAttribute("vos",getQuestions(0,0,10));
        return "index";
    }

    public List<ViewObject> getQuestions(int userId,int offset,int limit){
        List<Question> questions = questionService.getLatestQuestions(userId, offset, limit);
        List<ViewObject> vos = new ArrayList<ViewObject>();
        for (Question question: questions
                ) {
            ViewObject vo = new ViewObject();
            vo.set("question",question);
            vo.set("user",userService.getUser(question.getUserId()));

            vo.set("followCount", followService.getFollowerCount(EntityType.ENTITY_QUESTION, question.getId()));
            vos.add(vo);
        }
        return vos;
    }
}
