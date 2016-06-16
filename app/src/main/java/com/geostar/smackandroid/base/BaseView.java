package com.geostar.smackandroid.base;

/**
 * 仅负责视图更新，通过Presenter 拿去数据
 * @author jianghanghang
 *
 * @param <T>
 */
public interface BaseView<T> {

	void setPresenter(T presenter);
	
}
