/**
 * Copyright (c) 2015-2016, Silly Boy 胡建洪(1043244432@qq.com).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package me.huding.luobo.admin;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import me.huding.luobo.BaseController;
import me.huding.luobo.Parameters;
import me.huding.luobo.ResConsts;
import me.huding.luobo.model.Blog;
import me.huding.luobo.model.BlogTags;
import me.huding.luobo.utils.DateStyle;
import me.huding.luobo.utils.DateUtils;
import me.huding.luobo.utils.KeyUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.IAtom;


/**
 * 博客管理控制器
 * 
 * 完成博客发表，浏览，编辑，删除等博文管理功能
 *
 * @author JianhongHu
 * @version 1.0
 * @date 2016年10月28日
 */
public class BlogController extends BaseController {
	/**
	 * 日志记录器
	 */
	public static final Logger LOG = LoggerFactory.getLogger(BlogController.class);

	/**
	 * 发表博文Action
	 */
	public void publish(){
		/* 获取参数  */
		final Blog blog = getModel(Blog.class, "blog");
		String tagsStr = getPara("tags");
		final String[] tags = tagsStr.split(",");
		
		
		/* 校验参数 */
		// TODO validate params

		String signature = KeyUtils.signByMD5(blog.getTitle());
		// 查询签名是否已经存在
		if(Blog.findBySignature(signature) != null){
			setCode(ResConsts.Code.EXISTS);
			render();
			return;
		} 
		blog.setSignature(signature);

		final String blogID = KeyUtils.getUUID();
		/* 数据处理 */
		blog.setId(blogID);
		Date curDate = DateUtils.getCurrentDate();
		blog.setPublishTime(curDate);
		blog.setLastUpdateTime(curDate);

		String fileName = genHtmlFileName(curDate);
		blog.setPath(genHtmlFilePath(fileName));
		blog.setUrl(genHtmlURL(fileName));

		/* 静态化处理 */
		
		boolean st = statics(blog);
		// 静态化处理失败
		if(!st){
			setCode(ResConsts.Code.STATICS_ERROR);
			render();
			return;
		}
		
		
		// 持久化到数据库
		boolean flag = Db.tx(new IAtom() {
			@Override
			public boolean run() throws SQLException {
				boolean f1 = blog.save();
				boolean f2 = true;
				for(String tagID : tags){
					BlogTags blogTags = new BlogTags();
					blogTags.setBlogID(blogID);
					blogTags.setTagID(tagID);
					if(!(f2 = blogTags.save())){
						break;
					}
				}
				return f1 && f2;
			}
		});
		
		// 
		if(!flag){
			setCode(ResConsts.Code.FAILURE);
		} else {
			setCode(ResConsts.Code.SUCCESS);
		}
		render();
	}

	/**
	 * 使用时间生成博文的文件名，精确到秒
	 * @param date
	 * @return
	 */
	private String genHtmlFileName(Date date){
		if(date == null){
			date = DateUtils.getCurrentDate();
		}
		String prefix = DateUtils.DateToString(date, DateStyle.YYYYMMDDHHMMSS);
		return prefix + ".html";
	}

	/**
	 * 生成静态化的HTML的文件路径
	 * @return
	 */
	private String genHtmlFilePath(String value){
		return Parameters.STATICS_FINAL_PATH + File.separator + value;
	}

	/**
	 * 生成静态化的HTML的相对URL
	 * @param value
	 * @return
	 */
	private String genHtmlURL(String value){
		return Parameters.ARTICLES_PATH + "/" + value;
	}



	/**
	 * 
	 */
	public static final String PARAM_CHECK = "validateValue";

	/**
	 * 检查文章是否重复
	 */
	public void checkDuplicate(){
		String value = getPara(PARAM_CHECK);
		// 校验参数
		if(StrKit.isBlank(value)){

			return;
		}

		String signature = KeyUtils.signByMD5(value);
		// 查询签名是否已经存在
		Blog blog = Blog.findBySignature(signature);
		// 文章不存在
		if(blog == null){

		} 
		// 文章已存在
		else {

		}
	}

	/**
	 * 分页显示博文
	 */
	public void display(){

	}


	/**
	 * 查看博文详情
	 */
	public void look(){

	}

	/**
	 * 编辑博文
	 */
	public void edit(){

	}

	/**
	 * 删除博文
	 */
	public void delete(){

	}


	/**
	 * 暂存博文
	 */
	public void stage(){

	}


	/**
	 * 将所有的博文重新静态化
	 */
	public void reStaticsAll(){
		List<Blog> blogs = Blog.findAll("id,html,author,title,publishTime,path");
		for(Blog blog : blogs){
			statics(blog);
		}
		render(ResConsts.Code.SUCCESS);
	}
	
	/**
	 * 
	 * @param blog
	 * @return
	 */
	private boolean statics(Blog blog){
		/* 静态化处理 */
		StaticsBean.Builder builder = new StaticsBean.Builder();
		builder.setID(blog.getId()).setAuthor(blog.getAuthor());
		builder.setContent(blog.getHtml());
		builder.setPublishTime(blog.getPublishTime()).setTitle(blog.getTitle());
		boolean st = false;
		try {
			st = StaticsUtils.render(blog.getPath(), builder.build());
		} catch(IOException e){
			LOG.error(e.getMessage(),e);
		}
		return st;
	}




}
