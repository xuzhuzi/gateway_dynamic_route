package com.example.gateway_dynamic_route.controller;

import com.example.gateway_dynamic_route.DynamicRouteEventPublisher;
import com.example.gateway_dynamic_route.model.pojo.Router;
import com.example.gateway_dynamic_route.service.RouterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.route.RouteDefinition;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequestMapping("/gateway/route")
public class GatewayDynamicRouteController {
	@Autowired
	private DynamicRouteEventPublisher dynamicRouteEventPublisher;
	@Autowired
	private RouterService routerService;

	@GetMapping("/list")
	public List<Router> list() {
		List<Router> routers = routerService.listAll();
		return routers;
	}
	@PostMapping("/add")
	public String create(@RequestBody RouteDefinition entity) {
		dynamicRouteEventPublisher.add(entity);
		return "success";
	}

	@PostMapping("/update")
	public String update(@RequestBody RouteDefinition entity) {
		dynamicRouteEventPublisher.update(entity);
		return "success";
	}

	/**
	 * 刷新路由
	 * @return
	 */
	@GetMapping("/refresh_route")
	public Mono<String> refreshRoute() {
		// 刷新路由
		return dynamicRouteEventPublisher.notifyChanged().thenReturn("success");
	}

	@GetMapping("/delete/{id}")
	public String delete(@PathVariable String id) {
		dynamicRouteEventPublisher.delete(id);
		return "success";
	}
}