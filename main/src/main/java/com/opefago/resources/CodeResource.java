package com.opefago.resources;

import com.codahale.metrics.annotation.Timed;
import com.opefago.lib.common.types.Language;
import com.opefago.lib.models.command.RunCodeCommand;
import com.opefago.services.CodeService;
import org.apache.http.client.HttpResponseException;

import javax.inject.Inject;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.UUID;

@Path("/v1/code")
@Produces(MediaType.APPLICATION_JSON)
public class CodeResource {
    private final CodeService codeService;

    @Inject
    public CodeResource(final CodeService codeService){
        this.codeService = codeService;
    }

    @POST
    @Timed
    public Response publishCode(@Valid RunCodeCommand command) throws HttpResponseException {
        return Response.ok(codeService.publishCode(command)).build();
    }

    @GET
    @Path("/{codeId}")
    @Timed
    public Response getStatus(@PathParam("codeId") final UUID codeId) throws HttpResponseException {
        return Response.ok(codeService.getCodeStatus(codeId)).build();
    }

    @GET
    @Path("languages-supported")
    @Timed
    public Response getCode() throws HttpResponseException {
        return Response.ok(Language.values()).build();
    }
}
