package com.example.formDemoVertX;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServer;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.StaticHandler;
import io.vertx.ext.web.templ.handlebars.HandlebarsTemplateEngine;

/**
 * https://vertx.io/get-started/
 *
 * mvn package
 *
 * java -jar target/formDemoVertX-1.0.0-SNAPSHOT-fat.jar
 */
public class MainVerticle extends AbstractVerticle {

  public static void main (String[] args) {
    Vertx vertx = Vertx.vertx();
    vertx.deployVerticle(new MainVerticle());
  }

  @Override
  public void start(Promise<Void> startPromise) throws Exception {
    HttpServer server = vertx.createHttpServer();

    Router router = Router.router(vertx);

    router.get("/greeting").handler(this::greeting);
    router.get("/*").handler(StaticHandler.create());

    server.requestHandler(router).listen(8888);

    System.out.println("Server is started on port 8888");
  }

  private void greeting(RoutingContext routingContext) {
    try {
      String robot = routingContext.request().getParam("robot");
      String name = routingContext.request().getParam("name");
      int age = Integer.valueOf(routingContext.request().getParam("age"));
      String basque = routingContext.request().getParam("basque");
      String spanish = routingContext.request().getParam("spanish");
      String english = routingContext.request().getParam("english");
      String german = routingContext.request().getParam("german");
      System.out.printf("%s\n%s\n%s\n%s\n%s\n%s\n", robot, name, basque, spanish, english, german);
      HandlebarsTemplateEngine engine = HandlebarsTemplateEngine.create(vertx);
      JsonObject data = new JsonObject()
        .put("name", name)
        .put("age", age);
      if (robot != null && robot.equals("yes")) data.put("robot", true);
      JsonArray greetings = new JsonArray();
      if (basque != null) greetings.add("Kaixo");
      if (spanish != null) greetings.add("Hola");
      if (english != null) greetings.add("Hello");
      if (german != null) greetings.add("Hallo");
      data.put("greetings", greetings);
      System.out.println(data.toString());
      engine.render(data, "templates/greeting.hbs", res -> {
        if (res.succeeded()) {
          routingContext.response().end(res.result());
        } else {
          routingContext.fail(res.cause());
        }
      });
    }catch (Exception e){
      routingContext.redirect("error.html");
    }

  }

}
