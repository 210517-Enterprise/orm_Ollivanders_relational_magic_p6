package com.ollivanders.service;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ollivanders.dao.WandDAO;
import com.ollivanders.model.Wand;
import com.ollivanders.model.Wizard;

public class WandService {
	
	private static final Logger log = LoggerFactory.getLogger(WandService.class);
	private static WandDAO wandDAO = new WandDAO();
	
	/**
	 * Save wand as record in wand table
	 * @param wand built in BuildWandPortal
	 * @param wizard that is purchasing the wand
	 * @return wand build but including the id
	 */
	public Wand save(Wand wand, Wizard wizard) {
		if(wizard.getId() < 1) {
			System.out.println("Invalid Wizard");
			log.debug("Invalid Wizard ID");
		}
		
		log.info("Wizard " + wizard.getId() + " is buying " + wand.toString());
		
		try {
			wand.setWizard_id(wizard.getId());
			Wand resultWand = wandDAO.save(wand);
			
			log.info("Wand was purchased");
			
			return resultWand;
			
		} catch(Exception e) {
			log.error("Unable to purchase Wand, purchase should be made void "+e);
		}
		return new Wand();
	}
	
	/**
	 * 
	 * @return all wands from wand table
	 */
	public List<Wand> getAll() {
		List<Wand> wands = new ArrayList<Wand>();
		try {
			wands = wandDAO.getAll();
			log.info("Grabbed all wands");
		} catch (Exception e) {
			log.debug("Unable to grab all wands");
		}
		return wands;
		
	}
}
