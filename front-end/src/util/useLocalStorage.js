import { useEffect, useState } from "react";

function useLocalState (defaultValue, key) {
    const[value, setValue] = useState(() => {
        const localStorageVlaue = localStorage.getItem(key);
        return localStorageVlaue !== null ? JSON.parse(localStorageVlaue) : defaultValue;
    })

    useEffect(() => {
        localStorage.setItem(key, JSON.stringify(value))
    }, [key, value]);
    return [value, setValue];
}

export {useLocalState}