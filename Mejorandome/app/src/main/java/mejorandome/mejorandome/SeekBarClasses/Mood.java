package mejorandome.mejorandome.SeekBarClasses;


public class Mood {

    private int felicidad;
    private int paz;
    private int satisfaccion;
    private int angustia;
    private int culpa;
    private int ansiedad;
    private int peso;

    public int getFelicidad() {
        return felicidad;
    }

    public void setFelicidad(int felicidad) {
        this.felicidad = felicidad;
    }

    public int getPaz() {
        return paz;
    }

    public void setPaz(int paz) {
        this.paz = paz;
    }

    public int getSatisfaccion() {
        return satisfaccion;
    }

    public void setSatisfaccion(int satisfaccion) {
        this.satisfaccion = satisfaccion;
    }

    public int getAngustia() {
        return angustia;
    }

    public void setAngustia(int angustia) {
        this.angustia = angustia;
    }

    public int getCulpa() {
        return culpa;
    }

    public void setCulpa(int culpa) {
        this.culpa = culpa;
    }

    public int getAnsiedad() {
        return ansiedad;
    }

    public void setAnsiedad(int ansiedad) {
        this.ansiedad = ansiedad;
    }

    public int getPeso() {
        return peso;
    }

    public void setPeso(int peso) {
        this.peso = peso;
    }


    public boolean AllReady()
    {
        if(felicidad == -1) return false;
        if(paz == -1) return false;
        if(satisfaccion == -1) return false;
        if(angustia == -1) return false;
        if(culpa == -1) return false;
        if(ansiedad == -1) return false;
        if(peso < 30) return false;

        return true;
    }

    public Mood()
    {
        felicidad = -1;
        paz = -1;
        satisfaccion = -1;
        angustia = -1;
        culpa = -1;
        ansiedad = -1;
        peso = -1;
    }

}
