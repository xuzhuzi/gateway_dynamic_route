package com.example.gateway_dynamic_route.model.pojo;

import javax.persistence.*;

/**
 *  定义路由实体
 */
@Entity
@Table(name = "route")
public class Router {

	/**
	 * 路由ID
	 **/
	@Id
	private String id;

	/**
	 * 路由断言
	 **/
	private String predicates;
	/**
	 * 过滤器
	 **/
	private String filters;
	/**
	 * 跳转地址uri
	 **/
	private String uri;
	/**
	 * 路由顺序
	 **/
	@Column(name = "route_order")
	private int routeOrder;


	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getPredicates() {
		return predicates;
	}

	public void setPredicates(String predicates) {
		this.predicates = predicates;
	}

	public String getFilters() {
		return filters;
	}

	public void setFilters(String filters) {
		this.filters = filters;
	}

	public String getUri() {
		return uri;
	}

	public void setUri(String uri) {
		this.uri = uri;
	}

	public int getRouteOrder() {
		return routeOrder;
	}

	public void setRouteOrder(int routeOrder) {
		this.routeOrder = routeOrder;
	}
}