import React from 'react';
import { useLocalState } from '../util/useLocalStorage';

const Homepage = () => {
    const [jwt, setJwt] = useLocalState("", "jwt");
    return (
        <div>
            <div className='App'>
                <h1>Dashboard</h1>
                <div>JWT value: {jwt}</div>
            </div>
        </div>
    );
};

export default Homepage