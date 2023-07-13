package com.redhat.training.expenses;

import java.util.List;
import java.util.UUID;

import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path( "/expenses" )
@Consumes( MediaType.APPLICATION_JSON )
@Produces( MediaType.APPLICATION_JSON )
public class ExpenseResource {

    @Inject
    ExpenseValidator validator;

    @GET
    public List<Expense> list() {
        return Expense.listAll();
    }

    @POST
    @Transactional
    public Expense create( final Expense expense ) {
        Expense newExpense = Expense.of( expense.name, expense.paymentMethod, expense.amount.toString() );

        if ( !validator.isValid( newExpense ) ) {
            throw new WebApplicationException( Response.Status.BAD_REQUEST );
        }

        newExpense.persist();

        return newExpense;
    }

    @DELETE
    @Path( "{uuid}" )
    @Transactional
    public List<Expense> delete( @PathParam( "uuid" ) final UUID uuid ) {
        long numExpensesDeleted = Expense.delete( "uuid", uuid );

        if ( numExpensesDeleted == 0 ) {
            throw new WebApplicationException( Response.Status.NOT_FOUND );
        }

        return Expense.listAll();
    }

    @PUT
    @Transactional
    public void update( final Expense expense ) {
        if ( expense.id != null )
            Expense.update( expense );
        else
            throw new NotFoundException( "Expense id not provided." );
    }
}