package com.example.gateway_dynamic_route;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cloud.gateway.event.RefreshRoutesEvent;
import org.springframework.cloud.gateway.route.RouteDefinition;
import org.springframework.cloud.gateway.route.RouteDefinitionRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

/**
 * 动态路由服务
 */
@Service
@Slf4j
public class DynamicRouteEventPublisher implements ApplicationEventPublisherAware {

	@Autowired
	@Qualifier("dynamicRouteDefinitionRepository")
	private RouteDefinitionRepository routeDefinitionWriter;
	private ApplicationEventPublisher publisher;

	@Override
	public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
		this.publisher = applicationEventPublisher;
	}

	/**
	 * 增加路由
	 * @param definition
	 * @return
	 */
	public String add(RouteDefinition definition) {
		routeDefinitionWriter.save(Mono.just(definition)).subscribe();
		notifyChanged();
		return "success";
	}

	/**
	 * 更新路由
	 * @param definition
	 * @return
	 */
	public String update(RouteDefinition definition) {
		try {
			routeDefinitionWriter.save(Mono.just(definition)).subscribe();
			notifyChanged();
			return "success";
		} catch (Exception e) {
			return "update route  fail";
		}
	}

	/**
	 * 删除路由
	 * @param id
	 * @return
	 */
	public String delete(String id) {
		this.routeDefinitionWriter.delete(Mono.just(id)).subscribe();
		notifyChanged();
		return "success";
	}

	/**
	 * 刷新路由
	 */
	public Mono<Void> notifyChanged() {
		this.publisher.publishEvent(new RefreshRoutesEvent(this));
		return Mono.empty();
	}
}