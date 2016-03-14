/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.banco.rest;

import com.banco.ejb.ClienteFacade;
import com.banco.entidades.Cliente;
import com.banco.pagination.PaginatedListWrapper;

import javax.ejb.EJB;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.enterprise.context.RequestScoped;
import javax.ws.rs.core.MediaType;

/**
 * REST Web Service
 *
 * @author jmartinez
 */
@Path("clientes")
@RequestScoped
public class ClientesResource {

    @Context
    private UriInfo context;

    @EJB
    ClienteFacade facadeCliente;

    /**
     * Creates a new instance of ClientesResource
     */
    public ClientesResource() {
    }

    /**
     * Retrieves representation of an instance of com.banco.rest.ClientesResource
     * @return an instance of com.banco.entidades.Cliente
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public PaginatedListWrapper listClientes(@DefaultValue("1")
                                            @QueryParam("page")
                                            Integer page,
                                             @DefaultValue("id")
                                            @QueryParam("sortFields")
                                            String sortFields,
                                             @DefaultValue("asc")
                                            @QueryParam("sortDirections")
                                            String sortDirections) {
        PaginatedListWrapper paginatedListWrapper = new PaginatedListWrapper();
        paginatedListWrapper.setCurrentPage(page);
        paginatedListWrapper.setSortFields(sortFields);
        paginatedListWrapper.setSortDirections(sortDirections);
        paginatedListWrapper.setPageSize(10);
        return findClientes(paginatedListWrapper);
    }

    private PaginatedListWrapper findClientes(PaginatedListWrapper wrapper) {
        int totalClientes = facadeCliente.count();
        wrapper.setTotalResults(totalClientes);
        int start = (wrapper.getCurrentPage() - 1) * wrapper.getPageSize();
        wrapper.setList(facadeCliente.findRange(start,
                wrapper.getPageSize(),
                wrapper.getSortFields(),
                wrapper.getSortDirections()));
        return wrapper;
    }


/*
    @SuppressWarnings("unchecked")
    private List<Cliente> findClientes(int startPosition, int maxResults, String sortFields, String sortDirections) {

        Query query =
                entityManager.createQuery("SELECT p FROM Person p ORDER BY p." + sortFields + " " + sortDirections);
        query.setFirstResult(startPosition);
        query.setMaxResults(maxResults);
        return query.getResultList();
    }

*/
    @GET
    @Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Cliente consultarCliente(@PathParam("id") Long id){
        return facadeCliente.find(id);

    }

    /**
     * PUT method for updating or creating an instance of ClientesResource
     * @param cliente representation for the resource
     */
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public void guardarCliente(Cliente cliente) {
        try {
            if (cliente.getId() == null) {
                facadeCliente.crearCliente(cliente);
            }else {
                facadeCliente.modificarCliente(cliente);
            }
        }catch (Exception e){
            //TODO Definir manejo
        }
    }
    
    @DELETE
    @Path("{id}")
    public void eliminarCliente(@PathParam("id") Long id){
        try {
            facadeCliente.eliminarCliente(id);
        }catch (Exception e){
            //TODO Definir manejo
        }

    }
    

}
