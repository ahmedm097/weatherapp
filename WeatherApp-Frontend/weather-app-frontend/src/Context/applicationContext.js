import React from "react";
import { useCookies } from "react-cookie";

export const AppContext = React.createContext();

const AppContextProvider = ({ children }) => {
  const [cookies, setCookie, removeCookie] = useCookies(["userData"]);
  const setSession = (userData) => {
    setCookie("userData", userData, {
      path: "/",
      maaxAge: 900,
    });
  };
  const getSession = () => {
    const userData = cookies.userData || null;
    return userData;
  };
  const logout = () => {
    removeCookie("userData", { path: "/" });
  };
  return (
    <AppContext.Provider
      value={{
        setSession,
        getSession,
        logout,
      }}
    >
      {children}
    </AppContext.Provider>
  );
};

export default AppContextProvider;
