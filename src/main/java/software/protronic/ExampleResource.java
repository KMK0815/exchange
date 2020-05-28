package software.protronic;

import java.util.concurrent.CompletableFuture;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.jboss.resteasy.annotations.jaxrs.PathParam;

import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.client.WebClient;
import io.vertx.ext.web.client.WebClientOptions;

@Path("/hello")
public class ExampleResource {

  @Inject
  Vertx vertx;

  private WebClient client;

  @PostConstruct
  void initialize() {
    this.client = WebClient.create(vertx,
        new WebClientOptions().setDefaultHost("fruityvice.com").setDefaultPort(443).setSsl(true).setTrustAll(true));
  }

  @GET
  @Produces(MediaType.APPLICATION_JSON)
  @Path("/{name}")
  public JsonObject getFruitData(@PathParam("name") String name) {
    CompletableFuture<JsonObject> ret = new CompletableFuture<>();
    client.get("/api/fruit/" + name).send(ar -> {
      if (ar.succeeded()) {
        ret.complete(ar.result().bodyAsJsonObject());
      } else {
        ret.complete(new JsonObject().put("code", ar.result().statusCode()).put("message", ar.result().bodyAsString()));
      }
    });
    return ret.join();
  }

}
