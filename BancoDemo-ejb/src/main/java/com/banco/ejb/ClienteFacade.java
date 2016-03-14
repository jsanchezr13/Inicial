/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.banco.ejb;

import com.banco.entidades.Cliente;
import com.banco.entidades.TipoIdentificacion;
import com.banco.exceptions.BancoException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

/**
 *
 * @author jmartinez
 */
@Stateless
public class ClienteFacade  extends AbstractFacade<Cliente> {
    @PersistenceContext(unitName = "BancoPU")
    private EntityManager em;
    
    Logger log = Logger.getLogger(this.getClass().getName());

    protected EntityManager getEntityManager() {
        return em;
    }

    public ClienteFacade() {
        super(Cliente.class);
    }
    
    public void crearCliente(Cliente cliente) throws BancoException{
        log.info("Inicia crearCliente(...)");
       //Se verifica si el cliente ya existe;
        Cliente cte = findByIdentificacion(cliente.getTipoIdentificacion(), cliente.getIdentificacion());
        if(cte!= null){
            em.lock(cte, LockModeType.PESSIMISTIC_FORCE_INCREMENT);
            log.warning("Cliente "+ cliente.getIdentificacion() + " ya existe !!");
            throw new BancoException("El cliente " +cliente.getTipoIdentificacion()+ "-" + cliente.getIdentificacion() +" ya existe en el sistema");
        }
        em.persist(cliente);
        log.info("Finaliza crearCliente(...)");
        
        //super.create(cliente);
    }
    
    
    @TransactionAttribute(TransactionAttributeType.SUPPORTS)
    public Cliente findByIdentificacion(TipoIdentificacion tId, long identificacion){
        log.log(Level.FINE,"Consulta cliente {0}",  identificacion);
        Query  q = em.createNamedQuery("Cliente.findByIdentificacion");
        q.setParameter("tipoIdentificacion", tId);
        q.setParameter("identificacion", identificacion);
        try{
           return ((Cliente)q.getSingleResult());
        }catch(NoResultException nre){
            log.log(Level.WARNING,"No se encontro cliente {0}", identificacion);
            return null;
        }
    }
    
    public Cliente modificarCliente(Cliente cte){
       /* 
       if (em.contains(cte)){
            System.out.println("Cliente " + cte.getId() + " SI existe en el contexto de persistencia!" );
        }else{
            System.out.println("Cliente " + cte.getId() + " NO existe en el contexto de persistencia!" );
        }*/
        log.log(Level.FINE,"Modificando cliente con nombre : {0} - Version: {1}", new Object[]{cte.getNombre(),cte.getVersion()} );
        
        cte = em.merge(cte);
        return cte;
        
    }
    
@TransactionAttribute(TransactionAttributeType.SUPPORTS)
    public Cliente findById(long id){
        log.log(Level.FINE,"Consulta cliente {0}",  id);
        Query  q = em.createNamedQuery("Cliente.findById");
        q.setParameter("id", id);
        try{
           return ((Cliente)q.getSingleResult());
        }catch(NoResultException nre){
            log.log(Level.WARNING,"No se encontro cliente {0}", id);
            return null;
        }
    }
    


    public void eliminarCliente(long id) {
        log.info("Inicia eliminarCliente(...)");
       //Se verifica si el cliente ya existe;
        Cliente cte = findById(id);
        em.remove(cte);
        log.info("Finaliza eliminarCliente(...)");
    }

    

    
    
    
}
