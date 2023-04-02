package com.example.gateway_dynamic_route;

import com.example.gateway_dynamic_route.model.pojo.Router;
import com.example.gateway_dynamic_route.service.RouterService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.FilterDefinition;
import org.springframework.cloud.gateway.handler.predicate.PredicateDefinition;
import org.springframework.cloud.gateway.route.RouteDefinition;
import org.springframework.cloud.gateway.route.RouteDefinitionRepository;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

/**
 * 动态路由持久化器
 */
@Component
@Slf4j
public class DynamicRouteDefinitionRepository implements RouteDefinitionRepository {
	@Autowired
	private RouterService routerService;

	private static ObjectMapper objectMapper = new ObjectMapper();
	/**
	 * 从自定义的数据源中获取所有的路由关系，
	 * 1. 刷新路由时会自动调这个方法获取route信息
	 * 2. 启动服务时也会调这个方法
	 * @return
	 */
	@Override
	public Flux<RouteDefinition> getRouteDefinitions() {
		// 获取数据源
		List<Router> routers = routerService.listAll();
		// 转换成 RouteDefinition
		List<RouteDefinition> routeDefinitions = new ArrayList<>();
		for (Router router : routers) {
			RouteDefinition routeDefinition = new RouteDefinition();
			routeDefinition.setId(router.getId());
			try {
				routeDefinition.setFilters(objectMapper.readValue(router.getFilters(), new TypeReference<List<FilterDefinition>>() {}));
			} catch (JsonProcessingException e) {
				log.error("设置 filters 失败，异常信息:{}", e);
			}
			try {
				routeDefinition.setPredicates(objectMapper.readValue(router.getFilters(), new TypeReference<List<PredicateDefinition>>() {}));
			} catch (JsonProcessingException e) {
				log.error("设置 predicates 失败，异常信息:{}", e);
			}
			routeDefinition.setUri(URI.create(router.getUri()));
			routeDefinition.setOrder(router.getRouteOrder());
			routeDefinitions.add(routeDefinition);
		}
		return Flux.fromIterable(routeDefinitions);
	}

	/**
	 * 保存路由关系至数据源中
	 * @param route
	 * @return
	 */
	@Override
	public Mono<Void> save(Mono<RouteDefinition> route) {
		return route.flatMap(routeDefinition -> {
			try {
				Router router = new Router();
				router.setId(routeDefinition.getId());
				router.setFilters(objectMapper.writeValueAsString(routeDefinition.getFilters()));
				router.setPredicates(objectMapper.writeValueAsString(routeDefinition.getPredicates()));
				router.setUri(routeDefinition.getUri().toString());
				router.setRouteOrder(routeDefinition.getOrder());
				routerService.add(router);
				return Mono.empty();
			} catch (Exception e) {
				log.error("保存路由信息失败，异常信息:{}", e);
				throw new RuntimeException("保存路由信息失败");
			}
		});
	}

	/**
	 * 删除数据源中的路由关系
	 * @param routeId
	 * @return
	 */
	@Override
	public Mono<Void> delete(Mono<String> routeId) {
		return routeId.flatMap(id -> {
			routerService.delete(id);
			return Mono.empty();
		});
	}
}
