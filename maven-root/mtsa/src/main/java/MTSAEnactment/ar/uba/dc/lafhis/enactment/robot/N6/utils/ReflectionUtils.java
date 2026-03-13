package MTSAEnactment.ar.uba.dc.lafhis.enactment.robot.N6.utils;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class ReflectionUtils {
	
    public static List<Class> findSubClasses(String tosubclassname, Object ... initArgs) {
    	ArrayList<Class> subClasses	= new ArrayList<Class>();
        try {
            Class tosubclass = Class.forName(tosubclassname);
            Package [] pcks = Package.getPackages();
            for (int i=0;i<pcks.length;i++) {
            	subClasses.addAll(findSubClasses(pcks[i].getName(),tosubclass, initArgs));
            }
        } catch (ClassNotFoundException ex) {
            System.err.println("Class "+tosubclassname+" not found!");
        }
        return subClasses;
    }	
	
    public static List<Class> findSubClasses(String pckgname, String tosubclassname, Object ... initArgs) {
    	try{
	    	return findSubClasses(pckgname, Class.forName(tosubclassname), initArgs);
	    } catch (ClassNotFoundException ex) {
	        System.err.println("Class "+tosubclassname+" not found!");
	    }
    	return null;
    }
    
    public static List<Class> findSubClasses(String pckgname, Class toSubClass, Object ... initArgs) {
    	ArrayList<Class> subClasses	= new ArrayList<Class>();
        String name = new String(pckgname);
        if (!name.startsWith("/")) {
            name = "/" + name;
        }        
        name = name.replace('.','/');
        
        // Get a File object for the package
        URL url = ReflectionUtils.class.getResource(name);
        File directory = new File(url.getFile());
        // New code
        // ======
        if (directory.exists()) {
            // Get the list of the files contained in the package
            String [] files = directory.list();
            for (int i=0;i<files.length;i++) {
                 
                // we are only interested in .class files
                if (files[i].endsWith(".class")) {
                    // removes the .class extension
                    String classname = files[i].substring(0,files[i].length()-6);
                    try {
                        // Try to create an instance of the object
                    	Class c		= Class.forName(pckgname+"."+classname);
                    	Class argsClasses[]		= new Class[initArgs.length];
                    	for(int j = 0; j < initArgs.length; j++){
                    		argsClasses[j] = initArgs[j].getClass();
                    	}
                    	Constructor ctor	= getCompatibleConstructor(c, argsClasses);  
                    	if(ctor != null){
	                        Object o 	= ctor.newInstance(initArgs);
	                        if (toSubClass.isInstance(o)) {
	                            subClasses.add(c);
	                        }
                    	}
                    } catch (ClassNotFoundException cnfex) {
                        System.err.println(cnfex);
                    } catch (InstantiationException iex) {
                        // We try to instantiate an interface
                        // or an object that does not have a 
                        // default constructor
                    } catch (IllegalAccessException iaex) {
                        // The class is not public
                    } catch (SecurityException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IllegalArgumentException e) {
						// TODO Auto-generated catch block
						//e.printStackTrace();
					} catch (InvocationTargetException e) {
						// TODO Auto-generated catch block
						//e.printStackTrace();
					}
                }
            }
        }
        return subClasses;
    }
    
    /**
    * Get a compatible constructor to the supplied parameter types.
    *
    * @param clazz the class which we want to construct
    * @param parameterTypes the types required of the constructor
    *
    * @return a compatible constructor or null if none exists
    */
    public static Constructor<?> getCompatibleConstructor(Class<?> clazz, Class<?>[] parameterTypes)
    {
      Constructor<?>[] constructors = clazz.getConstructors();
      for (int i = 0; i < constructors.length; i++)
      {
        if (constructors[i].getParameterTypes().length == (parameterTypes != null ? parameterTypes.length : 0))
        {
          // If we have the same number of parameters there is a shot that we have a compatible
          // constructor
          Class<?>[] constructorTypes = constructors[i].getParameterTypes();
          boolean isCompatible = true;
          for (int j = 0; j < (parameterTypes != null ? parameterTypes.length : 0); j++)
          {
            if (!constructorTypes[j].isAssignableFrom(parameterTypes[j]))
            {
              // The type is not assignment compatible, however
              // we might be able to coerce from a basic type to a boxed type
              if (constructorTypes[j].isPrimitive())
              {
                if (!isAssignablePrimitiveToBoxed(constructorTypes[j], parameterTypes[j]))
                {
                  isCompatible = false;
                  break;
                }
              }
            }
          }
          if (isCompatible)
          {
            return constructors[i];
          }
        }
      }
      return null;
    }
     
    /**
    * <p> Checks if a primitive type is assignable with a boxed type.</p>
    *
    * @param primitive a primitive class type
    * @param boxed     a boxed class type
    *
    * @return true if primitive and boxed are assignment compatible
    */
    private static boolean isAssignablePrimitiveToBoxed(Class<?> primitive, Class<?> boxed)
    {
      if (primitive.equals(java.lang.Boolean.TYPE))
      {
        if (boxed.equals(java.lang.Boolean.class))
          return true;
        else
          return false;
      }
      else
      {
        if (primitive.equals(java.lang.Byte.TYPE))
        {
          if (boxed.equals(java.lang.Byte.class))
            return true;
          else
            return false;
        }
        else
        {
          if (primitive.equals(java.lang.Character.TYPE))
          {
            if (boxed.equals(java.lang.Character.class))
              return true;
            else
              return false;
          }
          else
          {
            if (primitive.equals(java.lang.Double.TYPE))
            {
              if (boxed.equals(java.lang.Double.class))
                return true;
              else
                return false;
            }
            else
            {
              if (primitive.equals(java.lang.Float.TYPE))
              {
                if (boxed.equals(java.lang.Float.class))
                  return true;
                else
                  return false;
              }
              else
              {
                if (primitive.equals(java.lang.Integer.TYPE))
                {
                  if (boxed.equals(java.lang.Integer.class))
                    return true;
                  else
                    return false;
                }
                else
                {
                  if (primitive.equals(java.lang.Long.TYPE))
                  {
                    if (boxed.equals(java.lang.Long.class))
                      return true;
                    else
                      return false;
                  }
                  else
                  {
                    if (primitive.equals(java.lang.Short.TYPE))
                    {
                      if (boxed.equals(java.lang.Short.class))
                        return true;
                      else
                        return false;
                    }
                  }
                }
              }
            }
          }
        }
      }
      return false;
    }    
}
