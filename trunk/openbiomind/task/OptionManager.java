package task;

import java.util.*;

/**
 * Manages CLI options.
 * @author Lucio
 *
 */
class OptionManager{
    
      private Set<String> optionalSet=new HashSet<String>();
      private Set<String> missingMandatory=new HashSet<String>();
      private Set<String> unmatchingOptions=new HashSet<String>();
      private Set<String> unvaluedOptions=new HashSet<String>();
      private Map<String,String> optionMap=new HashMap<String,String>();
    
      /**
       * Handy method for displaying sets of options in error messages.
       * @param options
       * @return
       */
      public static String optionsToString(Set<String> options){
          
             StringBuffer buffer=new StringBuffer();
              
             for (String option:options){
                 buffer.append(" ");
                 buffer.append(option);
             }
             return buffer.toString().trim();
      }
      
      public OptionManager(Set<String> mandatoryOptions,Set<String> optionalOptions,String[] args){
          
             Set<String> existingOptions=new HashSet<String>();
          
             this.optionalSet=optionalOptions;
             for (int i=0;i<args.length;i++){
                 
                 String arg=args[i];
                 
                 if (arg.charAt(0)=='-'){
                    existingOptions.add(arg);
                    if ((!mandatoryOptions.contains(arg))&&(!optionalOptions.contains(arg))){
                       unmatchingOptions.add(arg);
                    }
                    else {
                         if (i+1>=args.length){
                            unvaluedOptions.add(arg);
                            continue;
                         }
                         if (args[i+1].charAt(0)=='-'){
                            unvaluedOptions.add(arg);
                            continue;
                         }
                         this.optionMap.put(arg,args[i+1]);
                    }
                 }
             }
             for (String option:mandatoryOptions){
                 if (!existingOptions.contains(option)){
                    missingMandatory.add(option);
                 }
             }
      }
    
      public Set<String> getOptionalSet(){
             return this.optionalSet;
      }
      
      public boolean containsOption(String option){
             return this.optionMap.containsKey(option);
      }
      
      public String getOption(String option){
             return this.optionMap.get(option);
      }
      
      public void setOption(String option,String value){
             this.optionMap.put(option,value);
      }
      
      public Set<String> getMissingMandatory(){
             return this.missingMandatory;
      }
      
      public Set<String> getUnmatchingOptions(){
             return this.unmatchingOptions;
      }
      
      /**
       * Returns a string of error messages concerning the parameters.
       * @return
       */
      public String makeErrorMessages(){
          
             StringBuffer buffer=new StringBuffer();
             
             if (getMissingMandatory().size()>0){
                buffer.append("Mandatory option(s) missing: "+OptionManager.optionsToString(getMissingMandatory())+"\n");
             }
             if (getUnmatchingOptions().size()>0){
                buffer.append("Unknown option(s): "+OptionManager.optionsToString(getUnmatchingOptions())+"\n");
             }
             if (this.unvaluedOptions.size()>0){
                buffer.append("Values missing for options: "+OptionManager.optionsToString(this.unvaluedOptions)+"\n");
             }
             return buffer.toString();
      }
      
}