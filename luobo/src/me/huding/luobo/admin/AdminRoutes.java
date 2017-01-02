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

import com.jfinal.config.Routes;

/**
 * 
 *
 *
 * @author JianhongHu
 * @version 1.0
 * @date 2016年10月28日
 */
public class AdminRoutes extends Routes {
	
	public static final String PREFIX = "/admin";

	@Override
	public void config() {
		add("/admin",MainController.class);
		add("/admin/blog",BlogController.class);
		add("/admin/upload",UploadController.class);
		add("/admin/category",CategoryController.class);
		add("/admin/tags",TagsController.class);
	}

}
