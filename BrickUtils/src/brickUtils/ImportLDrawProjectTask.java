/*
	Copyright 2013-2014 Mario Pascucci <mpascucci@gmail.com>
	This file is part of BrickUtils.

	BrickUtils is free software: you can redistribute it and/or modify
	it under the terms of the GNU General Public License as published by
	the Free Software Foundation, either version 3 of the License, or
	(at your option) any later version.

	BrickUtils is distributed in the hope that it will be useful,
	but WITHOUT ANY WARRANTY; without even the implied warranty of
	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
	GNU General Public License for more details.

	You should have received a copy of the GNU General Public License
	along with BrickUtils.  If not, see <http://www.gnu.org/licenses/>.

*/


package brickUtils;


import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.logging.Logger;

import javax.swing.SwingWorker;

import bricksnspace.ldrawlib.LDPrimitive;
import bricksnspace.ldrawlib.LDrawColor;
import bricksnspace.ldrawlib.LDrawCommand;
import bricksnspace.ldrawlib.LDrawLib;
import bricksnspace.ldrawlib.LDrawParser;
import bricksnspace.ldrawlib.LDrawPart;
import bricksnspace.ldrawlib.LDrawPartType;







/*
 * Imports in background an LDraw project file
 * @see javax.swing.SwingWorker
 */
public class ImportLDrawProjectTask extends SwingWorker<Integer, Void> {

	File ldr;
	LDrawLib ldrlib;
	private boolean warnings;
//	private String internalLog = "";		// internal log for errors and warnings
	private String mainModelName;
	private String mainModelDescription;

	public ImportLDrawProjectTask(File dat,	LDrawLib ldl) {
		
		ldrlib = ldl;
		ldr = dat;
	}
	
	
	public String getModelName() {
		return mainModelName;
	}
	
	
	
	public String getMainModelDescription() {
		return mainModelDescription;
	}

	
	
	public boolean isWarnings() {
		return warnings;
	}


//	public String getInternalLog() {
//		return internalLog;
//	}
//

//	private void addLogLine(String file, int line, String message) {
//
//		warnings = true;
//		if (line != 0)
//			internalLog += "[" +file + "] line# "+ line + "> " + message + "\n";
//		else 
//			internalLog += "[" +file + "] >" + message + "\n";
//		//System.out.println(internalLog); // DB
//			
//	}
	
	
	private void expandPrimitives(Collection<LDPrimitive> ldprim, int colorIndex) throws SQLException {
		
		for (LDPrimitive pr : ldprim) {
			if (pr.getType() == LDrawCommand.REFERENCE) {
				int color = colorIndex;
				if (pr.getColorIndex() != LDrawColor.CURRENT) {
					color = pr.getColorIndex();
				}
				LDrawPartType ldpt;
				if (LDrawPart.existsPart(pr.getLdrawId())) {
					ldpt = LDrawPart.getPart(pr.getLdrawId()).getPartType();
				}
				else {
					ldpt = LDrawPartType.NONE;
				}
				if (ldpt == LDrawPartType.OFFICIAL ||
						ldpt == LDrawPartType.UNOFFICIAL ||
						ldpt == LDrawPartType.SHORTCUT ||
						ldpt == LDrawPartType.UNOFF_SHORTCUT) {
					Brick b = PartMapping.getBrickByLdrId(pr.getLdrawId());
					b.color = BrickColor.getMapByLdr(color);
					b.quantity = 1;
					b.tmpAdd();
				}
//				else if (ldpt == LDrawPartType.CUSTOM_PART) {
//					LDrawPart pt = LDrawPart.getCustomPart(pr.getLdrawId());
//					Brick b = PartMapping.getBrickByLdrId(pt.getLdrawId());
//					b.color = BrickColor.getMapByLdr(color);
//					b.quantity = 1;
//					b.name = pt.getDescription();
//					b.tmpAdd();
//				}
				else if (ldpt == LDrawPartType.MODEL || ldpt == LDrawPartType.SUBMODEL) {
					LDrawPart pt = LDrawPart.getCustomPart(pr.getLdrawId());
					//System.out.println("Expand(recursive): " + pt); // DB
					expandPrimitives(pt.getPrimitives(), color);
				}
			}
		}

	}
	
	
	
	private void listParts(List<LDPrimitive> mainModel, LDrawPartType mainModelType) throws SQLException {
		
		switch (mainModelType) {
		case MODEL:
			// a model, maybe needs to expand submodels
		case SUBMODEL:
			// like a model
			for (LDPrimitive ldp : mainModel) {
				//System.out.println(ldp); // DB
				if (ldp.getType() != LDrawCommand.REFERENCE)
					continue;
				LDrawPart p = LDrawPart.getPart(ldp.getLdrawId());
				if (p == null) 
					continue;
				switch (p.getPartType()) {
				case MODEL:
				case SUBMODEL:
					for (LDPrimitive pr : ldp.getPrimitives()) {
						if (pr.getType() == LDrawCommand.REFERENCE) {
							int color = ldp.getColorIndex();
							if (pr.getColorIndex() != LDrawColor.CURRENT) {
								color = pr.getColorIndex();
							}
							LDrawPartType ldpt;
							if (LDrawPart.existsPart(pr.getLdrawId())) {
								ldpt = LDrawPart.getCustomPart(pr.getLdrawId()).getPartType();
							}
							else {
								ldpt = LDrawPartType.NONE;
							}
							//System.out.println(ldpt); // DB
							if (ldpt == LDrawPartType.OFFICIAL ||
									ldpt == LDrawPartType.UNOFFICIAL ||
									ldpt == LDrawPartType.SHORTCUT ||
									ldpt == LDrawPartType.UNOFF_SHORTCUT) {
								Brick b = PartMapping.getBrickByLdrId(pr.getLdrawId());
								b.color = BrickColor.getMapByLdr(color);
								b.quantity = 1;
								b.tmpAdd();
							}
//							else if (ldpt == LDrawPartType.CUSTOM_PART) {
//								LDrawPart pt = LDrawPart.getCustomPart(pr.getLdrawId());
//								Brick b = PartMapping.getBrickByLdrId(pt.getLdrawId());
//								b.color = BrickColor.getMapByLdr(color);
//								b.quantity = 1;
//								b.name = pt.getDescription();
//								b.tmpAdd();
//							}
							else if (ldpt == LDrawPartType.MODEL || ldpt == LDrawPartType.SUBMODEL) {
								LDrawPart pt = LDrawPart.getCustomPart(pr.getLdrawId());
								//System.out.println("Expand: "+pt); // DB
								expandPrimitives(pt.getPrimitives(),pr.getColorIndex());
							}
						}
					}
					break;
//				case CUSTOM_PART:
//					Brick c = PartMapping.getBrickByLdrId(ldp.getLdrawId().toLowerCase());
//					c.color = BrickColor.getMapByLdr(ldp.getColorIndex());
//					c.name = ldp.getDescription();
//					c.quantity = 1;
//					c.tmpAdd();
//					break;
				case OFFICIAL:
				case SHORTCUT:
				case UNOFFICIAL:
				case UNOFF_SHORTCUT:
					Brick b = PartMapping.getBrickByLdrId(ldp.getLdrawId().toLowerCase());
					b.color = BrickColor.getMapByLdr(ldp.getColorIndex());
					b.quantity = 1;
					b.tmpAdd();
					break;
				default:
					break;
				}					
			}
			break;
		case OFFICIAL:
		case SHORTCUT:
		case UNOFFICIAL:
		case UNOFF_SHORTCUT:
			// single part file, no expand needed
			Brick b = PartMapping.getBrickByLdrId(mainModelName.toLowerCase());
			if (b != null) {
				b.tmpAdd();
			}
			break;
		default:
			// no action
			break;
		}
	}
	
	
	
	@Override
	protected Integer doInBackground() throws SQLException, IOException, BrickException {
		
		//int color;
		boolean isMpd;
		String modelDir,part;
		//Map<String,ArrayList<Brick>> subModel = new HashMap<String,ArrayList<Brick>>();
		List<LDPrimitive> mainModel = new ArrayList<LDPrimitive>();
		LDrawPartType mainModelType;
		//ArrayList<Brick> partList;
		LDPrimitive p;
		
		//LDrawPart.clearCustomParts();
		LineNumberReader lnr = new LineNumberReader(new FileReader(ldr));
		String line;
		int lineNo = 0;
		while ((line = lnr.readLine()) != null) {
			lineNo++;
		}
		try {
			lnr.close();
		} catch (IOException ex) {
			;
		}
		// we cheats about complete
		lineNo += 10;
		setProgress(0);
		// "zeroes" brick
		Brick.createTmpTable();
		isMpd = false;
		modelDir = ldr.getParent();
		lnr = new LineNumberReader(new FileReader(ldr));
		while ((line = lnr.readLine()) != null) {
			LDrawCommand type = LDrawParser.parseCommand(line);
			if (type == LDrawCommand.MPDFILE) {
				isMpd = true;
				break;
			}
		}
		try {
			lnr.close();
		} catch (IOException ex) {
			;
		}
		if (isMpd) {
			LDrawPart subModel = null;
			boolean firstLine = false;
			boolean isMainModel = false;
			boolean isFirstModel = true;
			boolean isSubModel = false;
			mainModelType = LDrawPartType.MODEL;
			LDrawPartType partType = LDrawPartType.MODEL;
			lnr = new LineNumberReader(new FileReader(ldr));
			while ((line = lnr.readLine()) != null) {
				LDrawCommand type = LDrawParser.parseCommand(line);
				try {
					switch (type) {
					case MPDFILE:
						// if a new FILE command is found without NOFILE first...
						if (isSubModel && !isMainModel) {
							subModel.setPartType(partType);
						}
						else if (isSubModel && isMainModel) {
							mainModelType = partType;
							isMainModel = false;
						}
						isSubModel = false;
						part = LDrawParser.parseMpdFile(line).toLowerCase();
	                    if (isFirstModel) {
	                    	//mainModel = LDrawModel.newLDrawModel(part);
	                    	isFirstModel = false;
	                    	isMainModel = true;
	                    	isSubModel = true;
	                    }
	                    else {
	                    	isSubModel = true;
		                    if (!LDrawPart.existsCustomPart(part)) {
		                    	subModel = LDrawPart.newCustomPart(part);
		                    }
		                    else {
		                        //------------------- duplicate submodel name
		    					warnings = true;
		    					Logger.getGlobal().warning("[" +ldr.getName() + "] line# "+ lnr.getLineNumber() +  "> " +  
		                    			"Duplicate sub-model name '" + part + "' in MPD");   
		                        continue;
		                    }
	                    }
	                    firstLine = true;
	                    if (!isMainModel)
	                    	partType = LDrawPartType.SUBMODEL;
	                    else 
	                    	partType = LDrawPartType.MODEL;
	                    break;
					case MPDNOFILE:
						if (!isSubModel && !isMainModel) {
							warnings = true;
							Logger.getGlobal().warning("[" +ldr.getName() + "] line# "+ lnr.getLineNumber() +  "> " + 
									"Displaced 'NOFILE' in MPD");   
						}
						if (isSubModel && !isMainModel) {
							subModel.setPartType(partType);
						}
						else if (isSubModel && isMainModel) {
							mainModelType = partType;
						}
						isMainModel = false;
						isSubModel = false;
						break;
					case FILETYPE:
						if (isSubModel && ! isMainModel) {
							subModel.setPartType(LDrawParser.parsePartType(line));
						}
						break;
					case COMMENT:
					case EMPTY:
						break;
					case TRIANGLE:
					case LINE:
					case AUXLINE:
					case QUAD:
						if (partType == LDrawPartType.MODEL
							|| partType == LDrawPartType.SUBMODEL) {
							partType = LDrawPartType.UNOFFICIAL;
						}
						break;
					case NAME:
						if (!isMainModel) {
							subModel.setPartName(LDrawParser.parsePartName(line));
						}
						else {
							mainModelName = LDrawParser.parsePartName(line);
						}
						break;
					case META_UNKNOWN:
						if (firstLine) {
							if (isMainModel) {
								mainModelDescription = LDrawParser.parseDescription(line);
							}
							else if (isSubModel) {
								subModel.setDescription(LDrawParser.parseDescription(line));
							}
							firstLine = false;
						}
						break;
					default:
						if (!isSubModel && !isMainModel) {
							warnings = true;
							Logger.getGlobal().warning("[" +ldr.getName() + "] line# "+ lnr.getLineNumber() +  "> " + 
									"Invalid MPD file format: primitive or command outside FILE..NOFILE block:\n"+line );
						}
						break;
					}
				}
				catch (IllegalArgumentException exc) {
					warnings = true;
					Logger.getGlobal().warning("[" +ldr.getName() + "] line# "+ lnr.getLineNumber() +  "> " + 
							exc.getLocalizedMessage());
				}
				if (isSubModel && ! isMainModel) {
					subModel.setPartType(partType);
				}
			}
			try {
				lnr.close();
			} catch (IOException ex) {
				;
			}
			lnr = new LineNumberReader(new FileReader(ldr));
			// now read model file
			isSubModel = false;
			isFirstModel = true;
			isMainModel = false;
			firstLine = false;
			while ((line = lnr.readLine()) != null) {
				setProgress(lnr.getLineNumber()*100/lineNo);
				LDrawCommand type = LDrawParser.parseCommand(line);
				try {
					switch (type) {
					case MPDFILE:
						// in case FILE command is found without NOFILE first
	                    if (isSubModel) {
	                        //--------- we are in submodel, close and prepare next submodel
	                        isSubModel = false;
	                        if (isMainModel) {
	                        	// end loading main model
	                        	isMainModel = false;
	                        }
	                    }
						part = LDrawParser.parseMpdFile(line).toLowerCase();
						//System.out.println(part);
	                    isSubModel = true;
	                    if (isFirstModel) {
	                    	isFirstModel = false;
	                    	isMainModel = true;
	                    }
	                    else {
	                    	subModel = LDrawPart.getCustomPart(part);
	                    }
	                    firstLine = true;
	                    break;
					case MPDNOFILE:
	                    if (isSubModel) {
	                        //--------- we are in submodel, close and prepare next submodel
	                        isSubModel = false;
	                        if (isMainModel) {
	                        	// end loading main model
	                        	isMainModel = false;
	                        }
	                    }		
	                    break;  // no parts alone admitted in MPD files, so we are always in submodel
					case REFERENCE:
						p = LDrawParser.parseLineType1(line.toLowerCase(),false);
						//System.out.println(p); //  debug
	                    if (LDrawPart.isLdrPart(p.getLdrawId()) || LDrawPart.existsCustomPart(p.getLdrawId())) {
	                    	if (isMainModel) {
	    						//System.out.println("MainModel - " +p);
	                    		// add to main model
	                    		mainModel.add(p);
	                    	}
	                    	else if (isSubModel) {
	                    		// add to current submodel/custom part
	    						//System.out.println(subModel.getLdrawid() + " - " +p);
	                    		subModel.addPart(p);
	                    	}
	                    	else { 
	        					warnings = true;
	        					Logger.getGlobal().warning("[" +ldr.getName() + "] line# "+ lnr.getLineNumber() +  "> " + 
	        							"Invalid MPD file format: primitive or command outside FILE..NOFILE block:\n"+line );
	                    	}
	                    }
	                    else {
	    					warnings = true;
	    					Logger.getGlobal().warning("[" +ldr.getName() + "] line# "+ lnr.getLineNumber() +  "> " + 
	    							"Unknown submodel or part: "+p.getLdrawId());
	                    }
						break;
					case TRIANGLE: 
					case AUXLINE:
					case LINE:
					case QUAD:
						break;
					default:
						break;
					}
				}
				catch (IllegalArgumentException exc) {
					warnings = true;
					Logger.getGlobal().warning("[" +ldr.getName() + "] line# "+ lnr.getLineNumber() +  "> " + 
							exc.getLocalizedMessage());
				}
			}
			try {
				lnr.close();
			} catch (IOException ex) {
				;
			}
			// now counts parts and expand submodels
			listParts(mainModel, mainModelType);

		}
		else {
			// a normal LDR/DAT file
			mainModelType = LDrawPartType.MODEL;
			boolean firstLine = false;
			lnr = new LineNumberReader(new FileReader(ldr));
			while ((line = lnr.readLine()) != null) {
				//System.out.println(line); // DB
				setProgress(lnr.getLineNumber()*100/lineNo);
				LDrawCommand type = LDrawParser.parseCommand(line);
				try {
					switch (type) {
					case REFERENCE:
						p = LDrawParser.parseLineType1(line,false);
	                    if (!LDrawPart.isLdrPart(p.getLdrawId())) {
	                    	// not a LDraw part, checks if it is a submodel
	                		File ld = new File(modelDir,p.getLdrawId());
	                		if (!ld.exists()) {
	                			// old part or error in file
	        					warnings = true;
	        					Logger.getGlobal().warning("[" +ldr.getName() + "] line# "+ lnr.getLineNumber() +  "> " + 
	        							"Unknown part: "+p.getLdrawId());   
	                		}
	                		else {
	                			//System.out.println("SubModel: " + part);
	                			if (!LDrawPart.existsCustomPart(p.getLdrawId())) {
	                				// 	a new submodel             
	                				LDrawPart pt = LDrawPart.newCustomPart(p.getLdrawId());
	                				expandSubFile(pt,modelDir);
	                			}
	                			mainModel.add(p);
	                		}
	                    }
	                    else {
	                    	// System.out.println("Part: " + p); // DB
	                    	mainModel.add(p);
	                    }
	                    break;
					case AUXLINE:
					case LINE:
					case TRIANGLE: 
					case QUAD:
						if (mainModelType == LDrawPartType.MODEL
						|| mainModelType == LDrawPartType.SUBMODEL) {
							mainModelType = LDrawPartType.UNOFFICIAL;
						}
						break;
					case META_UNKNOWN:
						if (firstLine) {
							mainModelDescription = LDrawParser.parseDescription(line);
							firstLine = false;
						}
						break;
					default:
						break;
					}
				}
				catch (IllegalArgumentException exc) {
					warnings = true;
					Logger.getGlobal().warning("[" +ldr.getName() + "] line# "+ lnr.getLineNumber() +  "> " + 
							exc.getLocalizedMessage());
				}
			}
			try {
				lnr.close();
			} catch (IOException ex) {
				;
			}
			listParts(mainModel, mainModelType);
		}
		return Brick.countTemp()[1];
	}


	
	private void expandSubFile(LDrawPart model, String modelDir) throws IOException {

		String line;
		
		File ld = new File(modelDir,model.getLdrawId());
		//System.out.println(model); // DB
		LDPrimitive p;
		boolean firstLine = true;
		LDrawPartType partType = LDrawPartType.SUBMODEL;
		LineNumberReader lnr = new LineNumberReader(new FileReader(ld));
		while ((line = lnr.readLine()) != null) {
			LDrawCommand type = LDrawParser.parseCommand(line);
			try {
				switch (type) {
				case REFERENCE:
					p = LDrawParser.parseLineType1(line,false);
	                if (!LDrawPart.isLdrPart(p.getLdrawId())) {
	                	// not a LDraw part, checks if it is a submodel
	            		File subFile = new File(modelDir,p.getLdrawId());
	            		if (!subFile.exists() || !subFile.canRead()) {
	            			// old part or error in file
	    					warnings = true;
	    					Logger.getGlobal().warning("[" +ldr.getName() + "] line# "+ lnr.getLineNumber() +  "> " + 
	    							"Unknown part: "+p.getLdrawId());   
	            		}
	            		else {
	            			//System.out.println("SubModel: " + part); // DB
	            			model.addPart(p);
	            			if (!LDrawPart.existsCustomPart(p.getLdrawId())) {
	            				// 	a new submodel             
	            				LDrawPart pt = LDrawPart.newCustomPart(p.getLdrawId());
	            				expandSubFile(pt,modelDir);
	            			}
	            		}
	                }
	                else {
	                	//System.out.println("Part: " + part); // DB
	                	model.addPart(p);
	                }
	                break;
				case AUXLINE:
				case LINE:
				case TRIANGLE: 
				case QUAD:
					if (partType == LDrawPartType.MODEL
						|| partType == LDrawPartType.SUBMODEL) {
						partType = LDrawPartType.UNOFFICIAL;
					}
					break;
				case META_UNKNOWN:
					if (firstLine) {
						model.setDescription(LDrawParser.parseDescription(line));
						//System.out.println("Sub: "+model.getDescription()); // DB
						firstLine = false;
					}
					break;
				default:
					break;
				}
			}
			catch (IllegalArgumentException exc) {
				warnings = true;
				Logger.getGlobal().warning("[" +ldr.getName() + "] line# "+ lnr.getLineNumber() +  "> " + 
						exc.getLocalizedMessage());
			}
		}
		model.setPartType(partType);
		try {
			lnr.close();
		} catch (IOException ex) {
			;
		}
	}
	
}

