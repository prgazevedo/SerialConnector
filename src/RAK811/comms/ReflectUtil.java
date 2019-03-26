package RAK811.comms;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

public class ReflectUtil
{


    public static String getSignature ( Method method )
    {
        return method.getName() + "("
                + getParametersNamesAsString(method) + " )" ;
    }

    public static String getParametersNamesAsString(Method method){
        Parameter[] params = method.getParameters();
        StringBuilder paramString = new StringBuilder();
        for(Parameter param : params) {
            paramString.append("<");
            paramString.append( param.getName() +">" );
            //paramString.append( param.getParameterizedType().getTypeName() );

        }
        return paramString.toString();
    }



}
