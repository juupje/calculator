/*
A C++ Program To evaluate a Definite Integral by Gauss Quadrature Formula.
*/


#include<iostream>  //Header file for cin & cout
#include<cmath>  //Header file for mathematical operartions
#include<iomanip> //Header file for precession

using namespace std;  //calling the standard directory

//Given Fi=unction of Integration
long double f(long double x)
{
    long double d;
    d=pow(x,3);
    return d;
}

//For Legendre's Polynomial Pn(x)
long double pn(long double a[],int n,int m,long double x)
{
    int i;
    long double p=0;
    if(m==0)
    {
        for(i=0;i<=n;i=i+2)
        {
            if(x==0)
                break;
            p+=a[i]*pow(x,i);
        }
    }
    else
    {
        for(i=1;i<=n;i=i+2)
        { 
            p+=a[i]*pow(x,i);
        } 
    }
    return p;
}

//Derivative of Pn(x)
long double dn(long double a[],int n,int m,long double x)
{
    int i;
    long double p=0;
    if(m==0)
    {
        for(i=2;i<=n;i=i+2)
        {
            if(x==0)
                break;
            p+=i*a[i]*pow(x,i-1);
        }
    }
    else
    {
        for(i=1;i<=n;i=i+2)
        {
            p+=i*a[i]*pow(x,i-1);
        }
    }
    return p;
}

long double* lcoef(int N) {
	long double** lcoef = (long double**) malloc((N+1) * sizeof(*lcoef));
	for (int i = 0; i < N+1; i++)
	{
		lcoef[i] = (long double*) malloc(sizeof(*lcoef[i]) * (N+1));
		for(int j = 0; j < N+1; j++)
			lcoef[i][j] = 0;
	}

	lcoef[0][0]=lcoef[1][1]=1;
	for(int n = 1; n < N; n++) {
		lcoef[n+1][0]=-n*lcoef[n-1][0]/(n+1);
		for(int m = 1; m <= N; m++)
			lcoef[n+1][m]=((2*n+1)*lcoef[n][m-1]-n*lcoef[n-1][m])/(n+1);
	}
	return lcoef[N];
}

//Main Function
int main()
{
    int n,m,i,N;
    double c,d;
    cout<<"Enter the value of n for Pn(x) : \n";
    cin>>n;
    cout<<"Enter the lower limit a of integration : \n";
    cin>>c;
    cout<<"Enter the upper limit b of integration : \n";
    cin>>d;

    if(n<=0)
        return 0;

    long double b,x,y[n],z[n],w[n],l,v,s,g=0,u[n];
    m=n%2;

    long double* a = lcoef(n);

    if(m==0)
    {
        cout<<"\nThe Legendre's Polynomial is : ";
        cout<<a[0];
        for(i=2;i<=n;i=i+2)
            cout<<" + ("<<setprecision(10)<< a[i]<<") X^"<<i;
    }
    else
    {
        cout<<"\nThe Legendre's Polynomial is : ";
        cout<<"("<<a[1]<<") X";
        for(i=3;i<=n;i=i+2)
            cout<<" + ("<<a[i]<<") X^"<<i;
    }
    cout<<endl;

    //Roots of Pn(x)
    for(i=0;i<n;i++)
    {
		double epsilon = 1E-15;
        z[i]=cos(3.14*(i+0.75)/(n+0.5));
        l=z[i];
		int counter = 0;
        do
        {
            s=l-(pn(a,n,m,l)/dn(a,n,m,l));
            v=l;
            l=s;
			if(counter++>100) {
				counter = 0;
				epsilon*=10;
			}
        }
        while(fabs(l-v)>epsilon);
        if(epsilon != 1E-15)
			cout << "For i=" << i << " used epsilon: " << epsilon << endl;
        y[i]=l;
        w[i]=2/((1-pow(l,2))*(dn(a,n,m,l)*dn(a,n,m,l)));
    }

    for(i=0;i<n;i++)
    {
        u[i]=((d-c)*y[i]/2)+(c+d)/2;
    }
    cout<<"Roots\t\t\t\t"<<"Weights\n";
    for(i=0;i<n;i++)
    {
        cout<<setprecision(15)<<y[i]<<"\t\t"<<setprecision(15)<<w[i]<<endl;
    }
    
    for(i=0;i<n;i++)
        g+=w[i]*f(u[i]);
    
    g=g*(d-c)/2;
    cout<<"The Value of Integration is = "<<setprecision(10)<<g<<endl;
    return 0;
}
